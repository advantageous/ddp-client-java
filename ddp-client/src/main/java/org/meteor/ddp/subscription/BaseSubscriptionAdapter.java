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
 * @author geoffc@gmail.com
 * @since 1/21/14 at 3:55 PM.
 */
public class BaseSubscriptionAdapter implements SubscriptionAdapter {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    protected final Map<String, SubscriptionCallback> callbackMap = new ConcurrentHashMap<>();

    protected final WebSocketClient webSocketClient;

    protected final ObjectConverter objectConverter;

    public BaseSubscriptionAdapter(final WebSocketClient webSocketClient,
                                   final ObjectConverter objectConverter) {

        this.objectConverter = objectConverter;
        this.webSocketClient = webSocketClient;
        this.webSocketClient.registerHandler(this);
    }

    @Override
    public void subscribe(final String subscriptionName,
                          final Object[] params,
                          final Class clazz,
                          final SubscriptionCallback callback) throws IOException {

        final Long subscriptionId = SEQUENCE.getAndIncrement();
        final String id = subscriptionId.toString();
        callbackMap.put(id, callback);
        objectConverter.register(subscriptionName, clazz);
        final SubscribeMessage message = new SubscribeMessage();
        message.setId(id);
        message.setName(subscriptionName);
        message.setParams(params);
        webSocketClient.send(message);
    }

    @Override
    public void subscribe(final String subscriptionName,
                          final Object[] params,
                          final SubscriptionCallback callback) throws IOException {

        subscribe(subscriptionName, params, Object.class, callback);
    }

    @Override
    public void unsubscribe(final String subscriptionId) throws IOException {
        this.callbackMap.remove(subscriptionId);
        final UnsubscribeMessage message = new UnsubscribeMessage();
        message.setId(subscriptionId);
        webSocketClient.send(message);
    }

    @MessageHandler
    public void handleReady(final ReadyMessage message) {
        for (final String sub : message.getSubs()) {
            final SubscriptionCallback callback = this.callbackMap.get(sub);
            if (callback != null) {
                callback.onReady(sub);
                this.callbackMap.remove(sub);
            }
        }
    }

    @MessageHandler
    public void handleNoSub(final NoSubscriptionMessage message) {
        final SubscriptionCallback callback = this.callbackMap.get(message.getId());
        if (callback != null) {
            callback.onFailure(message.getId(), message.getError());
            this.callbackMap.remove(message.getId());
        }
    }

    public ObjectConverter getObjectConverter() {
        return this.objectConverter;
    }
}
