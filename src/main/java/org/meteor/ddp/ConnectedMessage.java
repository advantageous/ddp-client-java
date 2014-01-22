package org.meteor.ddp;

/**
 * Message sent from the server indicating that the connection is successful and provides a session identifier.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:36 PM.
 */

public class ConnectedMessage {

    private String session;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
