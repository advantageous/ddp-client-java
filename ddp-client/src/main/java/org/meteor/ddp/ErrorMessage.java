package org.meteor.ddp;

/**
 * Message sent from the server indicating an error in a previous message.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 6:36 PM.
 */

public class ErrorMessage {

    private String reason;

    private Object offendingMessage;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getOffendingMessage() {
        return offendingMessage;
    }

    public void setOffendingMessage(Object offendingMessage) {
        this.offendingMessage = offendingMessage;
    }

    @Override
    public String toString() {
        return "Error{" +
                "reason='" + reason + '\'' +
                ", offendingMessage=" + offendingMessage +
                '}';
    }
}
