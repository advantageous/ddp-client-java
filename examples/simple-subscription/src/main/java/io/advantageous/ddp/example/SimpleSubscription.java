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

package io.advantageous.ddp.example;

import io.advantageous.ddp.*;
import org.glassfish.tyrus.client.ClientManager;
import io.advantageous.ddp.subscription.JsonObjectConverter;
import io.advantageous.ddp.subscription.MapSubscriptionAdapter;
import io.advantageous.ddp.subscription.Subscription;
import io.advantageous.ddp.subscription.message.AddedMessage;
import io.advantageous.ddp.subscription.message.ChangedMessage;

import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple example of a Meteor.js subscription.
 *
 * @author geoffc@gmail.com
 * @since 2/27/14 at 1:19 PM.
 */
public class SimpleSubscription {

    private static void printTab(Tab tab) {
        System.out.println("  Name: " + tab.getName());
        System.out.println("  Total: " + tab.getTotal());
        System.out.println();
    }

    public static void main(String[] args) {

        WebSocketContainer container = ClientManager.createClient();
        MessageConverter messageConverter = new JsonMessageConverter();

        DDPMessageEndpoint endpoint = new DDPMessageEndpointImpl(container, messageConverter);

        endpoint.registerHandler(ConnectedMessage.class, message ->
                System.out.println("connected to server! session: " + message.getSession()));


        Map<String, Map<String, Object>> dataMap = new HashMap<>();

        new MapSubscriptionAdapter(
                endpoint,
                new Subscription[]{
                        new Subscription("tabs", Tab.class)
                },
                new JsonObjectConverter(),
                dataMap
        );

        endpoint.registerHandler(AddedMessage.class, DDPMessageHandler.Phase.AFTER_UPDATE, message -> {
            Tab theNewlyAddedTab = (Tab) dataMap.get(message.getCollection()).get(message.getId());
            System.out.println("Added a new tab:");
            printTab(theNewlyAddedTab);
        });

        endpoint.registerHandler(ChangedMessage.class, DDPMessageHandler.Phase.AFTER_UPDATE, message -> {
            Tab theUpdatedTab = (Tab) dataMap.get(message.getCollection()).get(message.getId());
            System.out.println(String.format("Tab %s was modified:", message.getId()));
            printTab(theUpdatedTab);
        });

        try {
            endpoint.connect("ws://localhost:3000/websocket");
            endpoint.await();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static class Tab {

        private String name;

        private Number total;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Number getTotal() {
            return total;
        }

        public void setTotal(Number total) {
            this.total = total;
        }
    }
}
