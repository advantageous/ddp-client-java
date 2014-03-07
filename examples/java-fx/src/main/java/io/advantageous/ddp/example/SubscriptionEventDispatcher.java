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

package io.advantageous.ddp.example;

import com.google.common.eventbus.EventBus;
import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.subscription.message.AddedBeforeMessage;
import io.advantageous.ddp.subscription.message.AddedMessage;
import io.advantageous.ddp.subscription.message.ChangedMessage;
import io.advantageous.ddp.subscription.message.RemovedMessage;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

import static io.advantageous.ddp.DDPMessageHandler.Phase;

/**
 * Dispatch domain-specific events based on subscription events.
 *
 * @author geoffc@gmail.com
 * @since 2/3/14 at 12:09 PM.
 */
public class SubscriptionEventDispatcher {

    private final Map<String, SubscriptionEventHandler> handlerMap = new HashMap<>();

    @Inject
    public SubscriptionEventDispatcher(final EventBus eventBus,
                                       final DDPMessageEndpoint endpoint,
                                       final @Named("Local Data Map") Map<String, Map<String, Object>> dataMap) {

        this.register(WebApplicationConstants.TABS_COLLECTION_NAME, new SubscriptionEventHandler() {
            @Override
            public void handleAdded(final String key) {
                eventBus.post(new TabAddedEvent(key, (Tab) dataMap.get(WebApplicationConstants.TABS_COLLECTION_NAME).get(key)));
            }

            @Override
            public void handleChanged(final String key) {
                eventBus.post(new TabUpdatedEvent(key, (Tab) dataMap.get(WebApplicationConstants.TABS_COLLECTION_NAME).get(key)));
            }

            @Override
            public void handleRemoved(final String key) {
                eventBus.post(new TabRemovedEvent(key));
            }
        });

        endpoint.registerHandler(AddedMessage.class, Phase.AFTER_UPDATE, this::handleAdded);
        endpoint.registerHandler(AddedBeforeMessage.class, Phase.AFTER_UPDATE, this::handleAddedBefore);
        endpoint.registerHandler(ChangedMessage.class, Phase.AFTER_UPDATE, this::handleChanged);
        endpoint.registerHandler(RemovedMessage.class, Phase.AFTER_UPDATE, this::handleRemoved);

    }

    private void register(final String collection, final SubscriptionEventHandler handler) {
        handlerMap.put(collection, handler);
    }

    public void handleAdded(final AddedMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) handler.handleAdded(message.getId());
    }

    public void handleAddedBefore(final AddedBeforeMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) handler.handleAdded(message.getId());
    }

    public void handleChanged(final ChangedMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) handler.handleChanged(message.getId());
    }

    public void handleRemoved(final RemovedMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) handler.handleRemoved(message.getId());
    }

    public interface SubscriptionEventHandler {
        void handleAdded(String key);

        void handleChanged(String key);

        void handleRemoved(String key);
    }

}
