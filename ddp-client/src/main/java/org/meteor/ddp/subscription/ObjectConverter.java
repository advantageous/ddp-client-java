package org.meteor.ddp.subscription;

import com.google.gson.JsonObject;

import java.util.Map;

public interface ObjectConverter {

    void register(String subscriptionName, Class clazz);

    Object toObject(JsonObject fields, String collectionName);

    Object updateFields(Object record, Map<String, Object> fields);
}
