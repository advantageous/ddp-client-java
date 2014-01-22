package org.meteor.ddp.subscription;

/**
 * Message indicating that a document has been remotely removed from a subscription.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:17 PM.
 */

public class RemovedMessage {

    private String collection;

    private String id;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RemovedMessage{" +
                "collection='" + collection + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
