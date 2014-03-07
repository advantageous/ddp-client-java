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

package io.advantageous.ddp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class DDPMessageEndpointImpl extends Endpoint implements DDPMessageEndpoint {

    public static final String DDP_PROTOCOL_VERSION = "pre1";

    private static final Logger LOGGER = LoggerFactory.getLogger(DDPMessageEndpointImpl.class);

    private static final boolean TRACE = LOGGER.isTraceEnabled();

    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final boolean INFO = LOGGER.isInfoEnabled();

    private static final boolean WARN = LOGGER.isWarnEnabled();

    protected final Map<Class, Map<DDPMessageHandler.Phase, Set<DDPMessageHandler>>> handlerMap = new HashMap<>();

    private final WebSocketContainer container;

    private final MessageConverter messageConverter;

    private CountDownLatch latch;

    private Session websocketSession;

    private String ddpSessionId = null;

    @Inject
    public DDPMessageEndpointImpl(final WebSocketContainer container,
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

        //This will one day be a lambda, but for now, lambdas blow up Tyrus
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

    @Override
    public void disconnect() throws IOException {
        this.websocketSession.close();
    }

    @Override
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

    @SuppressWarnings("unchecked")
    private void notifyHandlers(final Object message) {
        final Map<DDPMessageHandler.Phase, Set<DDPMessageHandler>> mapOfHandlerSets = this.handlerMap.get(message.getClass());
        if (mapOfHandlerSets == null) return;
        for (DDPMessageHandler.Phase phase : DDPMessageHandler.Phase.values()) {
            Set<DDPMessageHandler> handlers = mapOfHandlerSets.get(phase);
            if (handlers == null) continue;
            for (final DDPMessageHandler handler : handlers) {
                if (TRACE) LOGGER.trace("notifying handler: " + handler);
                handler.onMessage(message);
            }
        }
    }

    @Override
    public void registerHandler(final Class messageType,
                                final DDPMessageHandler.Phase phase,
                                final DDPMessageHandler handler) {

        if (DEBUG) LOGGER.debug("registering handler: " + handler.getClass().getName());

        Map<DDPMessageHandler.Phase, Set<DDPMessageHandler>> mapOfHandlerSets = handlerMap.get(messageType);
        if (mapOfHandlerSets == null) {
            mapOfHandlerSets = new HashMap<>(3);
        }
        Set<DDPMessageHandler> handlers = mapOfHandlerSets.get(phase);
        if (handlers == null) {
            handlers = new HashSet<>();
        }
        handlers.add(handler);
        mapOfHandlerSets.put(phase, handlers);
        handlerMap.put(messageType, mapOfHandlerSets);
    }

    @Override
    public <T> void registerHandler(final Class<T> messageType,
                                    final DDPMessageHandler<T> handler) {
        registerHandler(messageType, DDPMessageHandler.Phase.UPDATE, handler);
    }

    @Override
    public void await() throws InterruptedException {
        this.latch = new CountDownLatch(1);
        this.latch.await();
    }

    @Override
    public void send(final Object message) throws IOException {
        if (DEBUG) LOGGER.debug("sending message: " + message);
        if (this.websocketSession == null || !this.websocketSession.isOpen()) {
            throw new IllegalStateException("you must connect before sending data.");
        }
        final String convertedMessage = messageConverter.toDDP(message);
        if (DEBUG) LOGGER.debug("sending message: " + convertedMessage);
        this.websocketSession.getAsyncRemote().sendText(convertedMessage);
    }
}
