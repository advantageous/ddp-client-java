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

package org.meteor.ddp.subscription.message;

import com.google.gson.JsonObject;

/**
 * Message indicating that a document has been remotely added to a subscription.
 *
 * @author geoffc@gmail.com
 * @since 1/17/14 at 6:17 PM.
 */
public class AddedMessage {

    private String collection;

    private String id;

    private JsonObject fields;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonObject getFields() {
        return fields;
    }

    public void setFields(JsonObject fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "Added{" +
                "collection='" + collection + '\'' +
                ", id='" + id + '\'' +
                ", fields=" + fields +
                '}';
    }
}
