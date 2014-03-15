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

package io.advantageous.ddp.subscription;

import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.DDPMessageHandler;
import io.advantageous.ddp.subscription.message.NoSubscriptionMessage;
import io.advantageous.ddp.subscription.message.ReadyMessage;
import io.advantageous.ddp.subscription.message.SubscribeMessage;
import io.advantageous.ddp.subscription.message.UnsubscribeMessage;

import javax.inject.Inject;
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

    protected final Map<String, Subscription.Callback> callbackMap = new ConcurrentHashMap<>();

    protected final ObjectConverter objectConverter;

    protected final DDPMessageEndpoint messageEndpoint;

    @Inject
    public BaseSubscriptionAdapter(final DDPMessageEndpoint messageEndpoint,
                                   final ObjectConverter objectConverter) {

        this.objectConverter = objectConverter;
        this.messageEndpoint = messageEndpoint;

        messageEndpoint.registerHandler(ReadyMessage.class, new DDPMessageHandler<ReadyMessage>() {
            @Override
            public void onMessage(ReadyMessage message) {
                for (final String sub : message.getSubs()) {
                    final Subscription.Callback callback = BaseSubscriptionAdapter.this.callbackMap.get(sub);
                    if (callback != null) {
                        callback.onReady(sub);
                        BaseSubscriptionAdapter.this.callbackMap.remove(sub);
                    }
                }
            }
        });
        messageEndpoint.registerHandler(NoSubscriptionMessage.class, new DDPMessageHandler<NoSubscriptionMessage>() {
            @Override
            public void onMessage(NoSubscriptionMessage message) {
                final Subscription.Callback callback = BaseSubscriptionAdapter.this.callbackMap.get(message.getId());
                if (callback != null) {
                    callback.onFailure(message.getId(), message.getError());
                    BaseSubscriptionAdapter.this.callbackMap.remove(message.getId());
                }
            }
        });

    }

    @Override
    public void subscribe(final Subscription subscription) throws IOException {
        final Long subscriptionId = SEQUENCE.getAndIncrement();
        final String id = subscriptionId.toString();
        final Subscription.Callback callback = subscription.getCallback();
        if (callback != null) {
            callbackMap.put(id, subscription.getCallback());
        }
        objectConverter.register(subscription.getSubscriptionName(), subscription.getClazz());
        final SubscribeMessage message = new SubscribeMessage();
        message.setId(id);
        message.setName(subscription.getSubscriptionName());
        message.setParams(subscription.getParams());
        messageEndpoint.send(message);
    }

    @Override
    public void unsubscribe(final String subscriptionId) throws IOException {
        this.callbackMap.remove(subscriptionId);
        final UnsubscribeMessage message = new UnsubscribeMessage();
        message.setId(subscriptionId);
        messageEndpoint.send(message);
    }

    public ObjectConverter getObjectConverter() {
        return this.objectConverter;
    }
}
