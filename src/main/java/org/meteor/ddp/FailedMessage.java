package org.meteor.ddp;

/**
 * Message sent from the server indicating that the connection has failed and provides a supported version.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:36 PM.
 */

public class FailedMessage {

    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
