package org.meteor.ddp.rpc;

/**
 * Message that calls an RPC method via DDP.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:17 PM.
 */
public class MethodMessage {

    private String method;

    private String id;

    private Object[] params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
