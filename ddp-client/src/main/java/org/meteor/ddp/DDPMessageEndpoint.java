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

public class DDPMessageEndpoint extends Endpoint {

    public static final String DDP_PROTOCOL_VERSION = "pre1";

    private static final Logger LOGGER = LoggerFactory.getLogger(DDPMessageEndpoint.class);

    private static final boolean TRACE = LOGGER.isTraceEnabled();

    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final boolean INFO = LOGGER.isInfoEnabled();

    private static final boolean WARN = LOGGER.isWarnEnabled();

    protected final Map<Class, List<InstanceMethodContainer>> handlerMap = new HashMap<>();

    private final WebSocketContainer container;

    private final MessageConverter messageConverter;

    private CountDownLatch latch;

    private Session websocketSession;

    private String ddpSessionId = null;

    public DDPMessageEndpoint(final WebSocketContainer container,
                              final MessageConverter converter) {

        this.container = container;
        this.messageConverter = converter;

        this.registerHandler(ConnectedMessage.class, message -> {
            if (DEBUG) LOGGER.debug("got connected message: " + message);
            ddpSessionId = message.getSession();
        });
        registerErrorHandler();
        registerFailedHandler();
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        if (INFO) LOGGER.info("websocket connected ... " + session.getId());

        this.websocketSession = session;
        websocketSession.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String rawMessage) {
                if (TRACE) LOGGER.trace("raw message: " + rawMessage);
                try {
                    final Object message = messageConverter.fromDDP(rawMessage);
                    if (message != null) notifyHandlers(message);
                } catch (UnsupportedMessageException e) {
                    if (WARN) LOGGER.warn("unhandled message from server: " + e.getMessage());
                }
            }
        });

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

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        if (INFO) LOGGER.info(String.format("websocket closed %s close because of %s", session.getId(), closeReason));
        latch.countDown();
    }

    public void disconnect() throws IOException {
        this.websocketSession.close();
    }

    public void connect(final String address) throws IOException, InterruptedException {
        try {
            container.connectToServer(this, ClientEndpointConfig.Builder.create().build(), new URI(address));
        } catch (DeploymentException e) {
            throw new IllegalStateException(e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected void registerErrorHandler() {
        this.registerHandler(ErrorMessage.class, message -> LOGGER.error(message.toString()));
    }

    protected void registerFailedHandler() {
        this.registerHandler(FailedMessage.class, message -> {
            throw new IllegalStateException("The server does not support the DDP version specified by this " +
                    "webSocketClient.  Server version: " + message.getVersion() + ", webSocketClient version: " +
                    DDP_PROTOCOL_VERSION);

        });
    }

    private void notifyHandlers(final Object message) {
        final List<InstanceMethodContainer> containers = this.handlerMap.get(message.getClass());
        if (containers == null) return;
        for (final InstanceMethodContainer container : containers) {
            try {
                container.getMethod().setAccessible(true);
                container.getMethod().invoke(container.getInstance(), message);
            } catch (IllegalAccessException e) {
                // We scan for only public methods, so this should never happen.
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e);
            }
        }
    }

    public <T> void registerHandler(final Class<T> messageType,
                                    final DDPMessageHandler<T> handler,
                                    final Phase phase) {
        if (DEBUG) LOGGER.debug("registering handler: " + handler.getClass().getName());

        final Method method;
        try {
            method = handler.getClass().getMethod("onMessage", Object.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }

        // Make sure there is only one method argument
        if (method.getParameterTypes().length != 1) {
            throw new IllegalArgumentException(
                    "handler methods may only have one argument; the message object");
        }

        List<InstanceMethodContainer> handlerMethods = handlerMap.get(messageType);
        if (handlerMethods == null) {
            handlerMethods = new LinkedList<>();
        }

        final InstanceMethodContainer val = new InstanceMethodContainer(method, handler, phase.ordinal());

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

    public <T> void registerHandler(final Class<T> messageType,
                                    final DDPMessageHandler<T> handler) {
        registerHandler(messageType, handler, Phase.UPDATE);
    }

    public void await() throws InterruptedException {
        this.latch = new CountDownLatch(1);
        this.latch.await();
    }

    public void send(final Object message) throws IOException {
        if (DEBUG) LOGGER.debug("sending message: " + message);
        if (this.websocketSession == null || !this.websocketSession.isOpen()) {
            throw new IllegalStateException("you must connect before sending data.");
        }
        final String convertedMessage = messageConverter.toDDP(message);
        if (DEBUG) LOGGER.debug("sending message: " + convertedMessage);
        this.websocketSession.getAsyncRemote().sendText(convertedMessage);
    }

    public static enum Phase {
        BEFORE_UPDATE, UPDATE, AFTER_UPDATE
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
