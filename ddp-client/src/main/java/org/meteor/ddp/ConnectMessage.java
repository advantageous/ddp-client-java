package org.meteor.ddp;

/**
 * Message sent from client to server to establish a DDP connection.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:36 PM.
 */
public class ConnectMessage {

    private String session;

    private String version;

    private String[] support;

    public ConnectMessage() {
    }

    public ConnectMessage(String version, String[] support) {
        this.version = version;
        this.support = support;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getSupport() {
        return support;
    }

    public void setSupport(String[] support) {
        this.support = support;
    }
}
