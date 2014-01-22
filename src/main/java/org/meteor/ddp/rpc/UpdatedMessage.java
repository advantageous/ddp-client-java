package org.meteor.ddp.rpc;

/**
 * Message that indicates what methods have completed and updated values on the server.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:17 PM.
 */
public class UpdatedMessage {

    private String[] methods;

    public String[] getMethods() {
        return methods;
    }
}
