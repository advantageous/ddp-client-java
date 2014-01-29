package org.meteor.ddp.subscription;

import org.meteor.ddp.MessageHandler;
import org.meteor.ddp.WebSocketClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base class for handling DDP subscriptions.
 *
 * @author gcc@smarttab.com
 * @since 1/21/14 at 3:55 PM.
 */
public class BaseSubscriptionAdapter implements SubscriptionAdapter {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    protected final Map<String, SubscriptionCallback> callbackMap = new ConcurrentHashMap<>();

    protected final WebSocketClient webSocketClient;

    public BaseSubscriptionAdapter(final WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        this.webSocketClient.registerHandler(this);
    }

    @Override
    public void subscribe(final String subscriptionName,
                          final Object[] params,
                          final SubscriptionCallback callback) throws IOException {

        final Long subscriptionId = SEQUENCE.getAndIncrement();
        final String id = subscriptionId.toString();
        callbackMap.put(id, callback);
        final SubscribeMessage message = new SubscribeMessage();
        message.setId(id);
        message.setName(subscriptionName);
        message.setParams(params);
        webSocketClient.send(message);
    }

    @Override
    public void unsubscribe(final String subscriptionId) throws IOException {
        callbackMap.remove(subscriptionId);
        final UnsubscribeMessage message = new UnsubscribeMessage();
        message.setId(subscriptionId);
        webSocketClient.send(message);
    }

    @MessageHandler(ReadyMessage.class)
    public void handleReady(final ReadyMessage message) {
        for (final String sub : message.getSubs()) {
            final SubscriptionCallback callback = callbackMap.get(sub);
            if (callback != null) {
                callback.onReady(sub);
                callbackMap.remove(sub);
            }
        }
    }

    @MessageHandler(NoSubscriptionMessage.class)
    public void handleNoSub(final NoSubscriptionMessage message) {
        final SubscriptionCallback callback = callbackMap.get(message.getId());
        if (callback != null) {
            callback.onFailure(message.getId(), message.getError());
            callbackMap.remove(message.getId());
        }
    }
}
