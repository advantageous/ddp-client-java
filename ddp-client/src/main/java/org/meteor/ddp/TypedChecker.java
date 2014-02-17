package org.meteor.ddp;

import com.google.gson.JsonObject;
import org.meteor.ddp.subscription.AddedBeforeMessage;
import org.meteor.ddp.subscription.AddedMessage;

import java.util.HashMap;
import java.util.Map;


public class TypedChecker {

    public static void main(String[] args) {

        final Map<String, JsonObject> map = new HashMap<>();

        TypedChecker c = new TypedChecker();
        c.registerHandler((AddedMessage message) -> {
            map.put("foo", message.getFields());
        });


        c.registerHandler((AddedBeforeMessage message) -> {
            map.put("bar", message.getFields());
        });

        System.out.println(map.get("foo").toString());

    }

    public <T> void registerHandler(final DDPMessageHandler<T> handler) {


        //Figure out T

        Object foo = new Object();
        Class[] classes = foo.getClass().getEnclosingMethod().getParameterTypes();

        System.out.println(classes.toString());
        System.out.println(handler.toString());


    }
}
