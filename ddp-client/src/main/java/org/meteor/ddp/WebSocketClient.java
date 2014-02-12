/*
 * Copyright (C) 2014. Geoffrey Chandler.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.meteor.ddp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class WebSocketClient {

    public static final String DDP_PROTOCOL_VERSION = "pre1";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClient.class);

    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final boolean INFO = LOGGER.isInfoEnabled();

    private static final boolean WARN = LOGGER.isWarnEnabled();

    protected final Map<Class, List<InstanceMethodContainer>> handlerMap = new HashMap<>();

    private final WebSocketContainer container;

    private final CountDownLatch latch;

    private final MessageConverter converter;

    private Session webSocketSession;

    private String ddpSessionId = null;

    public WebSocketClient(final WebSocketContainer container,
                           final MessageConverter converter) {

        this.container = container;
        this.converter = converter;
        this.latch = new CountDownLatch(1);
        this.registerHandler(new Object() {
            @MessageHandler
            public final void handleConnected(final ConnectedMessage message) {
                ddpSessionId = message.getSession();
            }
        });
        registerErrorHandler();
        registerFailedHandler();
    }

    public void registerErrorHandler() {
        this.registerHandler(new Object() {
            @MessageHandler
            public void handleError(final ErrorMessage message) {
                LOGGER.error(message.toString());
            }
        });
    }

    public void registerFailedHandler() {
        this.registerHandler(new Object() {
            @MessageHandler
            public void handleFailed(final FailedMessage message) {
                throw new IllegalStateException("The server does not support the DDP version specified by this " +
                        "webSocketClient.  Server version: " + message.getVersion() + ", webSocketClient version: " +
                        DDP_PROTOCOL_VERSION);
            }
        });
    }

    @OnOpen
    public void onOpen(final Session session) {
        if (INFO) LOGGER.info("Connected ... " + session.getId());

        this.webSocketSession = session;

        try {
            final ConnectMessage connectMessage =
                    new ConnectMessage(DDP_PROTOCOL_VERSION, new String[]{DDP_PROTOCOL_VERSION});
            if (ddpSessionId != null) {
                connectMessage.setSession(ddpSessionId);
            }
            send(connectMessage);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(final String rawMessage, final Session session) {

        if (DEBUG) LOGGER.debug("websocket session: " + session.toString());
        if (DEBUG) LOGGER.debug("raw message: " + rawMessage);

        try {
            final Object message = converter.fromDDP(rawMessage);
            if (message != null) notifyHandlers(message);
        } catch (UnsupportedMessageException e) {
            if (WARN) LOGGER.warn("unhandled message from server: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(final Session session, final CloseReason closeReason) {
        if (INFO) LOGGER.info(String.format("Session %s close because of %s", session.getId(), closeReason));
        this.webSocketSession = null;
        latch.countDown();
    }

    public void send(final Object message) throws IOException {
        if (this.webSocketSession == null) throw new IllegalStateException("you must connect before sending data.");
        final String convertedMessage = converter.toDDP(message);
        if (DEBUG) LOGGER.debug("sending message: " + convertedMessage);
        this.webSocketSession.getBasicRemote().sendText(convertedMessage);
    }

    public void disconnect() {
        latch.countDown();
    }

    public void connect(final String address) throws IOException, InterruptedException {
        try {
            container.connectToServer(this, new URI(address));
            latch.await();
        } catch (DeploymentException e) {
            throw new IllegalStateException(e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void notifyHandlers(final Object message) {
        final List<InstanceMethodContainer> containers = this.handlerMap.get(message.getClass());
        if (containers == null) return;
        for (final InstanceMethodContainer container : containers) {
            try {
                container.getMethod().invoke(container.getInstance(), message);
            } catch (IllegalAccessException ignore) {
                // We scan for only public methods, so this will never happen.
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public void registerHandler(final Object handler) {

        if (DEBUG) LOGGER.debug("registering handler: " + handler);

        final Method[] allMethods = handler.getClass().getMethods();
        for (final Method method : allMethods) {
            final MessageHandler annotation = method.getAnnotation(MessageHandler.class);

            if (annotation != null) {

                // Make sure there is only one method argument
                if (method.getParameterTypes().length != 1) {
                    throw new IllegalArgumentException(
                            "handler methods may only have one argument; the message object");
                }

                registerMethod(method.getParameterTypes()[0], method, handler, annotation.value().ordinal());
            }
        }
    }

    private void registerMethod(final Class messageType,
                                final Method method,
                                final Object instance,
                                final int order) {

        if (INFO) LOGGER.info("registering handler method: " + method);

        List<InstanceMethodContainer> handlerMethods = handlerMap.get(messageType);
        if (handlerMethods == null) {
            handlerMethods = new LinkedList<>();
        }

        final InstanceMethodContainer val = new InstanceMethodContainer(method, instance, order);

        //Put the item in the correct order in the list
        if (handlerMethods.size() == 0) {
            handlerMethods.add(val);
        } else if (handlerMethods.get(0).getOrder() > val.getOrder()) {
            handlerMethods.add(0, val);
        } else if (handlerMethods.get(handlerMethods.size() - 1).getOrder() < val.getOrder()) {
            handlerMethods.add(handlerMethods.size(), val);
        } else {
            int i = 0;
            while (handlerMethods.get(i).getOrder() < val.getOrder()) {
                i++;
            }
            handlerMethods.add(i, val);
        }

        handlerMap.put(messageType, handlerMethods);
    }

    protected static final class InstanceMethodContainer {
        private Method method;

        private Object instance;

        private int order;

        private InstanceMethodContainer(Method method, Object instance, int order) {
            this.method = method;
            this.instance = instance;
            this.order = order;
        }

        public Method getMethod() {
            return method;
        }

        public Object getInstance() {
            return instance;
        }

        public int getOrder() {
            return order;
        }
    }

}
