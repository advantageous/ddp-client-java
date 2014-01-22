package org.meteor.ddp.subscription;

/**
 * Message to unsubscribe from a subscription.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:05 PM.
 */

public class UnsubscribeMessage {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
