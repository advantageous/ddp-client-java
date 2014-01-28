package org.meteor.ddp.subscription;

import java.io.IOException;

/**
 * Interface for a meteor subscription adapter.
 *
 * @author gcc@smarttab.com
 * @since 1/22/14 at 3:23 PM.
 */
public interface SubscriptionAdapter {
    void subscribe(String subscriptionName,
                   Object[] params,
                   SubscriptionCallback callback) throws IOException;

    void unsubscribe(String subscriptionId) throws IOException;
}
