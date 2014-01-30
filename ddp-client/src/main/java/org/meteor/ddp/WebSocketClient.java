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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class WebSocketClient {

    public static final String DDP_PROTOCOL_VERSION = "pre1";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClient.class);

    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final boolean INFO = LOGGER.isInfoEnabled();

    private static final boolean WARN = LOGGER.isWarnEnabled();

    protected final Map<Class, Set<InstanceMethodContainer>> handlerMap = new HashMap<>();

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
            @MessageHandler(ConnectedMessage.class)
            public final void handleConnected(final ConnectedMessage message) {
                ddpSessionId = message.getSession();
            }
        });
        registerErrorHandler();
        registerFailedHandler();
    }

    public void registerErrorHandler() {
        this.registerHandler(new Object() {
            @MessageHandler(ErrorMessage.class)
            public void handleError(final ErrorMessage message) {
                LOGGER.error(message.toString());
            }
        });
    }

    public void registerFailedHandler() {
        this.registerHandler(new Object() {
            @MessageHandler(FailedMessage.class)
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
        final Set<InstanceMethodContainer> containers = this.handlerMap.get(message.getClass());
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

                // Make sure the annotations match the method parameters
                if (method.getParameterTypes().length != 1 || method.getParameterTypes()[0] != annotation.value()) {
                    throw new IllegalArgumentException("method handler annotations must match method parameter " +
                            "type. method: " + method.getName() + " class: " + handler.getClass().getCanonicalName());
                }

                registerMethod(annotation.value(), method, handler);
            }
        }
    }

    private void registerMethod(final Class messageType, final Method method, final Object instance) {

        if (INFO) LOGGER.info("registering handler method: " + method);

        Set<InstanceMethodContainer> handlerMethods = handlerMap.get(messageType);
        if (handlerMethods == null) {
            handlerMethods = new HashSet<>();
        }
        handlerMethods.add(new InstanceMethodContainer(method, instance));
        handlerMap.put(messageType, handlerMethods);
    }

    protected static final class InstanceMethodContainer {
        private Method method;

        private Object instance;

        private InstanceMethodContainer(Method method, Object instance) {
            this.method = method;
            this.instance = instance;
        }

        public Method getMethod() {
            return method;
        }

        public Object getInstance() {
            return instance;
        }
    }

}
