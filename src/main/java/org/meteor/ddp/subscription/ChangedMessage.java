package org.meteor.ddp.subscription;

import java.util.HashMap;

/**
 * Message indicating that a document has been remotely changed in a subscription.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:17 PM.
 */

public class ChangedMessage {

    private String collection;

    private String id;

    private HashMap<String, Object> fields;

    private String[] cleared;

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

    public HashMap<String, Object> getFields() {
        return fields;
    }

    public void setFields(HashMap<String, Object> fields) {
        this.fields = fields;
    }

    public String[] getCleared() {
        return cleared;
    }

    public void setCleared(String[] cleared) {
        this.cleared = cleared;
    }
}
