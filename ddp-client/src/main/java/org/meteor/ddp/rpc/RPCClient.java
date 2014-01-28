package org.meteor.ddp.rpc;

import java.io.IOException;

/**
 * description
 *
 * @author gcc@smarttab.com
 * @since 1/22/14 at 2:01 PM.
 */
public interface RPCClient {
    void call(String methodName,
              Object[] params,
              AsyncCallback<Object> callback) throws IOException;
}
