package org.meteor.ddp.subscription;


/**
 * Subscribe Message
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 5:13 PM.
 */

public class SubscribeMessage {

    private String id;

    private String name;

    private Object[] params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
