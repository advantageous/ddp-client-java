package org.meteor.ddp.rpc;

import org.meteor.ddp.DDPError;

/**
 * Message that indicates the result of an RPC call.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:17 PM.
 */
public class ResultMessage {

    private String id;

    private DDPError error;

    private Object result;

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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
