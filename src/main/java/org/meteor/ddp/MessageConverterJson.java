package org.meteor.ddp;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a DDP message converter for use with JSON payloads.
 *
 * @author gcc@smarttab.com
 * @since 1/17/14 at 9:27 PM.
 */
public class MessageConverterJson implements MessageConverter {

    private static final Gson GSON = new Gson();

    @Override
    public Object fromDDP(final String rawMessage) throws UnsupportedMessageException {
        final Map jsonMap = GSON.fromJson(rawMessage, HashMap.class);
        final Object msg = jsonMap.get("msg");
        if (msg != null) {
            final String ddpMessageType = msg.toString();
            final Class messageClass = MessageRegistry.get(ddpMessageType);
            if (messageClass == null) throw new UnsupportedMessageException(rawMessage);
            return GSON.fromJson(rawMessage, messageClass);
        }
        return null;
    }

    @Override
    public String toDDP(final Object object) {
        final String json = GSON.toJson(object, object.getClass());
        final StringBuilder buffer = new StringBuilder(json);
        buffer.insert(1, "\"msg\":\"" + MessageRegistry.get(object.getClass()) + "\",");
        return buffer.toString();
    }
}
