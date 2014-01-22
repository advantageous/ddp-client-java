package org.meteor.ddp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Subscribe to messages of a type.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 7:44 PM.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageHandler {

    public Class value();
}
