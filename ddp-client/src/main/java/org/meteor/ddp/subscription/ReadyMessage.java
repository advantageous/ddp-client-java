package org.meteor.ddp.subscription;

/**
 * Indicates that a subscription is ready. (All the data has been sent)
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:05 PM.
 */

public class ReadyMessage {

    private String[] subs;

    public String[] getSubs() {
        return subs;
    }

    public void setSubs(String[] subs) {
        this.subs = subs;
    }
}
