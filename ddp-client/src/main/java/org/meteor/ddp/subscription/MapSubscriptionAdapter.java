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

    private static final boolean INFO = LOGGER.isInfoEnabled();

    private static final boolean WARN = LOGGER.isWarnEnabled();

    private final Map<String, Map<String, Object>> dataMap;

    public MapSubscriptionAdapter(final WebSocketClient webSocketClient,
                                  final Map<String, Map<String, Object>> dataMap) {
        super(webSocketClient);
        this.dataMap = dataMap;
        this.webSocketClient.registerHandler(this);
    }

    @MessageHandler(AddedMessage.class)
    public void handleAdded(final AddedMessage message) {
        if (DEBUG) LOGGER.debug("got added message: " + message);
        Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection == null) {
            localCollection = new HashMap<>();
            dataMap.put(message.getCollection(), localCollection);
        }
        localCollection.put(message.getId(), message.getFields());
    }

    @MessageHandler(AddedBeforeMessage.class)
    public void handleAddedBefore(final AddedBeforeMessage message) {
        if (WARN) LOGGER.debug("received AddedBefore message.  The basic map subscription adapter does not support " +
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
        Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection == null) {
            localCollection = new HashMap<>();
            dataMap.put(message.getCollection(), localCollection);
        }

        final Object record = localCollection.get(message.getId());



        localCollection.put(message.getId(), message.getFields());
    }

    @MessageHandler(MovedBeforeMessage.class)
    public void handleMovedBefore(final MovedBeforeMessage message) {
        if (WARN) LOGGER.debug("received MovedBefore message.  The basic map subscription adapter does not support " +
                "ordering in collections.  This message will be ignored.", message);
    }

    @MessageHandler(RemovedMessage.class)
    public void handleRemoved(final RemovedMessage message) {
        if (DEBUG) LOGGER.debug("got removed message: " + message);
        Map<String, Object> localCollection = dataMap.get(message.getCollection());
        if (localCollection != null) {
            localCollection.remove(message.getId());
        }
    }

    protected Map<String, Map<String, Object>> getDataMap() {
        return dataMap;
    }
}
