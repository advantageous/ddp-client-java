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
import io.advantageous.ddp.subscription.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple subscription adapter to keep a map in sync with a meteor subscription.
 *
 * @author geoffc@gmail.com
 * @since 1/21/14 at 4:24 PM.
 */
public class MapSubscriptionAdapter extends BaseSubscriptionAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSubscriptionAdapter.class);

    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final boolean WARN = LOGGER.isWarnEnabled();

    private final Map<String, Map<String, Object>> dataMap;

    @Inject
    public MapSubscriptionAdapter(final DDPMessageEndpoint endpoint,
                                  final ObjectConverter objectConverter,
                                  final @Named("Local Data Map") Map<String, Map<String, Object>> dataMap) {

        super(endpoint, objectConverter);

        this.dataMap = dataMap;

        endpoint.registerHandler(AddedMessage.class, new DDPMessageHandler<AddedMessage>() {
            @Override
            public void onMessage(AddedMessage message) {
                handleAdded(message);
            }
        });
        endpoint.registerHandler(AddedBeforeMessage.class, new DDPMessageHandler<AddedBeforeMessage>() {
            @Override
            public void onMessage(AddedBeforeMessage message) {
                handleAddedBefore(message);
            }
        });
        endpoint.registerHandler(ChangedMessage.class, new DDPMessageHandler<ChangedMessage>() {
            @Override
            public void onMessage(ChangedMessage message) {
                handleChanged(message);
            }
        });
        endpoint.registerHandler(MovedBeforeMessage.class, new DDPMessageHandler<MovedBeforeMessage>() {
            @Override
            public void onMessage(MovedBeforeMessage message) {
                handleMovedBefore(message);
            }
        });
        endpoint.registerHandler(RemovedMessage.class, new DDPMessageHandler<RemovedMessage>() {
            @Override
            public void onMessage(RemovedMessage message) {
                handleRemoved(message);
            }
        });

    }

    public void handleAdded(final AddedMessage message) {
        if (DEBUG) LOGGER.debug(message.toString());
        Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection == null) {
            localCollection = new HashMap<>();
            dataMap.put(message.getCollection(), localCollection);
        }

        final Object value = super.getObjectConverter().toObject(message.getFields(), message.getCollection());
        localCollection.put(message.getId(), value);
    }

    public void handleAddedBefore(final AddedBeforeMessage message) {
        if (WARN) LOGGER.warn("received AddedBefore message.  The basic map subscription adapter does not support " +
                "ordering in collections.  The item will be treated as a regular Added event.", message);
        final AddedMessage addedMessage = new AddedMessage();
        addedMessage.setFields(message.getFields());
        addedMessage.setCollection(message.getCollection());
        addedMessage.setId(message.getId());
        this.handleAdded(addedMessage);
    }

    public void handleChanged(final ChangedMessage message) {
        if (DEBUG) LOGGER.debug(message.toString());
        final Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection == null) {
            throw new IllegalStateException("Received a changed message for an item that we don't have in our local " +
                    "map. Message: " + message);
        }
        final Object record = localCollection.get(message.getId());
        final Map<String, Object> fields = message.getFields();
        super.getObjectConverter().updateFields(record, fields);
        localCollection.put(message.getId(), record);
    }

    public void handleMovedBefore(final MovedBeforeMessage message) {
        if (WARN) LOGGER.warn("received MovedBefore message.  The basic map subscription adapter does not support " +
                "ordering in collections.  This message will be ignored: ", message);
    }

    public void handleRemoved(final RemovedMessage message) {
        if (DEBUG) LOGGER.debug(message.toString());
        final Map<String, ?> localCollection = dataMap.get(message.getCollection());
        if (localCollection != null) {
            localCollection.remove(message.getId());
        }
    }
}
