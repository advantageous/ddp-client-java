/*
 * Copyright (C) 2014. Geoffrey Chandler.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.meteor.ddp;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a DDP message converter for use with JSON payloads.
 *
 * @author geoffc@gmail.com
 * @since 1/17/14 at 9:27 PM.
 */
public class JsonMessageConverter implements MessageConverter {

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
