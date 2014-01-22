package org.meteor.ddp.subscription;

import org.meteor.ddp.DDPError;

/**
 * No Subscription Message
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:10 PM.
 */

public class NoSubscriptionMessage {

    private String id;

    private DDPError error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DDPError getError() {
        return error;
    }

    public void setError(DDPError error) {
        this.error = error;
    }
}
