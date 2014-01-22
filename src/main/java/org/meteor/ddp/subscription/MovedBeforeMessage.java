package org.meteor.ddp.subscription;

/**
 * Message indicating that a document has been remotely moved in a subscription at a specific position in the order.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:17 PM.
 */

public class MovedBeforeMessage {

    private String collection;

    private String id;

    private String before;

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

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }
}
