package org.meteor.ddp;

import org.meteor.ddp.rpc.MethodMessage;
import org.meteor.ddp.rpc.ResultMessage;
import org.meteor.ddp.rpc.UpdatedMessage;
import org.meteor.ddp.subscription.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for converting DDP messages to and from raw string types.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 7:08 PM.
 */
public interface MessageConverter {

    public Object fromDDP(String message) throws UnsupportedMessageException;

    public String toDDP(Object object);

    static class MessageRegistry {

        static final Map<String, Class> STRING_CLASS_HASH_MAP = new HashMap<>();

        static final Map<Class, String> CLASS_STRING_HASH_MAP = new HashMap<>();

        static void register(String name, Class clazz) {
            STRING_CLASS_HASH_MAP.put(name, clazz);
            CLASS_STRING_HASH_MAP.put(clazz, name);
        }

        static String get(Class clazz) {
            return CLASS_STRING_HASH_MAP.get(clazz);
        }

        static Class get(String key) {
            return STRING_CLASS_HASH_MAP.get(key);
        }

        static {
            register("method", MethodMessage.class);
            register("result", ResultMessage.class);
            register("updated", UpdatedMessage.class);

            register("added", AddedMessage.class);
            register("addedBefore", AddedBeforeMessage.class);
            register("changed", ChangedMessage.class);
            register("movedBefore", MovedBeforeMessage.class);
            register("nosub", NoSubscriptionMessage.class);
            register("ready", ReadyMessage.class);
            register("removed", RemovedMessage.class);
            register("sub", SubscribeMessage.class);
            register("unsub", UnsubscribeMessage.class);

            register("connect", ConnectMessage.class);
            register("connected", ConnectedMessage.class);
            register("error", ErrorMessage.class);
            register("failed", FailedMessage.class);
        }
    }
}
