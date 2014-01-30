package org.meteor.ddp.subscription;

import org.meteor.ddp.MessageHandler;
import org.meteor.ddp.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple subscription adapter to keep a map in sync with a meteor subscription.
 *
 * @author gcc@smarttab.com
 * @since 1/21/14 at 4:24 PM.
 */
public class MapSubscriptionAdapter extends BaseSubscriptionAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSubscriptionAdapter.class);

    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private static final boolean WARN = LOGGER.isWarnEnabled();

    private final Map<String, Map<String, Object>> dataMap;

    public MapSubscriptionAdapter(final WebSocketClient webSocketClient,
                                  final ObjectConverter objectConverter,
                                  final Map<String, Map<String, Object>> dataMap) {

        super(webSocketClient, objectConverter);
        this.dataMap = dataMap;
    }

    @MessageHandler(AddedMessage.class)
    public void handleAdded(final AddedMessage message) {
        if (DEBUG) LOGGER.debug("got added message: " + message);
        Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection == null) {
            localCollection = new HashMap<>();
            dataMap.put(message.getCollection(), localCollection);
        }

        localCollection.put(message.getId(),
                super.getObjectConverter().toObject(message.getFields(), message.getCollection()));
    }

    @MessageHandler(AddedBeforeMessage.class)
    public void handleAddedBefore(final AddedBeforeMessage message) {
        if (WARN) LOGGER.warn("received AddedBefore message.  The basic map subscription adapter does not support " +
                "ordering in collections.  The item will be treated as a regular Added event.", message);
        final AddedMessage addedMessage = new AddedMessage();
        addedMessage.setFields(message.getFields());
        addedMessage.setCollection(message.getCollection());
        addedMessage.setId(message.getId());
        this.handleAdded(addedMessage);
    }

    @MessageHandler(ChangedMessage.class)
    public void handleChanged(final ChangedMessage message) {
        if (DEBUG) LOGGER.debug("got changed message: " + message);
        final Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection == null) {
            throw new IllegalStateException("Received a changed message for an item that we don't have in our local " +
                    "map. Message: " + message);
        }
        final Object record = localCollection.get(message.getId());
        final Map<String, Object> fields = message.getFields();
        final Object updated = super.getObjectConverter().updateFields(record, fields);
        localCollection.put(message.getId(), updated);
    }

    @MessageHandler(MovedBeforeMessage.class)
    public void handleMovedBefore(final MovedBeforeMessage message) {
        if (WARN) LOGGER.warn("received MovedBefore message.  The basic map subscription adapter does not support " +
                "ordering in collections.  This message will be ignored: ", message);
    }

    @MessageHandler(RemovedMessage.class)
    public void handleRemoved(final RemovedMessage message) {
        if (DEBUG) LOGGER.debug("got removed message: " + message);
        final Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection != null) {
            localCollection.remove(message.getId());
        }
    }

    protected Map<String, Map<String, Object>> getDataMap() {
        return dataMap;
    }
}
