package org.meteor.ddp;

/**
 * An error thrown by the DDP server.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:12 PM.
 */
public class DDPError {

    private final int error;

    private final String reason;

    private final String details;

    public DDPError(final int error, final String reason, final String details) {
        this.error = error;
        this.reason = reason;
        this.details = details;
    }

    public int getError() {
        return error;
    }

    public String getReason() {
        return reason;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "DDPError{" +
                "error=" + error +
                ", reason='" + reason + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
