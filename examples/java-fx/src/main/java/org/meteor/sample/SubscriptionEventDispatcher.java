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

package org.meteor.sample;

import com.google.common.eventbus.EventBus;
import org.meteor.ddp.DDPMessageEndpoint;
import org.meteor.ddp.subscription.message.AddedBeforeMessage;
import org.meteor.ddp.subscription.message.AddedMessage;
import org.meteor.ddp.subscription.message.ChangedMessage;
import org.meteor.ddp.subscription.message.RemovedMessage;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

import static org.meteor.ddp.DDPMessageEndpoint.*;
import static org.meteor.sample.WebApplicationConstants.TABS_COLLECTION_NAME;

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

        this.register(TABS_COLLECTION_NAME, new SubscriptionEventHandler() {
            @Override
            public void handleAdded(final String key) {
                eventBus.post(new TabAddedEvent(key, (Tab) dataMap.get(TABS_COLLECTION_NAME).get(key)));
            }

            @Override
            public void handleChanged(final String key) {
                eventBus.post(new TabUpdatedEvent(key, (Tab) dataMap.get(TABS_COLLECTION_NAME).get(key)));
            }

            @Override
            public void handleRemoved(final String key) {
                eventBus.post(new TabRemovedEvent(key));
            }
        });

        endpoint.registerHandler(AddedMessage.class, this::handleAdded, Phase.AFTER_UPDATE);
        endpoint.registerHandler(AddedBeforeMessage.class, this::handleAddedBefore, Phase.AFTER_UPDATE);
        endpoint.registerHandler(ChangedMessage.class, this::handleChanged, Phase.AFTER_UPDATE);
        endpoint.registerHandler(RemovedMessage.class, this::handleRemoved, Phase.AFTER_UPDATE);

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
