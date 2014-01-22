package org.meteor.ddp.rpc;

import org.meteor.ddp.DDPError;

/**
 * Interface for a RPC callback.
 *
 * @author gcc@smarttab.com
 * @since 1/18/14 at 12:59 AM.
 */
public interface AsyncCallback<T> {

    void onSuccess(T result);

    void onFailure(DDPError message);
}
