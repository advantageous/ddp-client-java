package org.meteor.ddp.subscription;

import org.meteor.ddp.DDPError;

/**
 * Callback run when a subscription is handled.
 *
 * @author gcc@smarttab.com
 * @since 1/21/14 at 4:08 PM.
 */
public interface SubscriptionCallback {

    void onReady(String subscriptionId);

    void onFailure(String subscriptionId, DDPError error);
}
