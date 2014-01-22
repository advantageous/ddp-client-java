package org.meteor.ddp;

/**
 * Thrown when we get a message from the server that we don't know how to process.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 7:15 PM.
 */
public class UnsupportedMessageException extends Exception {
    public UnsupportedMessageException(String rawMessage) {
        super(rawMessage);
    }
}
