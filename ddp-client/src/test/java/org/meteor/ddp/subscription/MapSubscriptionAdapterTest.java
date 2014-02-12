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

package org.meteor.ddp.subscription;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Assert;
import org.junit.Test;
import org.meteor.ddp.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapSubscriptionAdapterTest {

    @Test
    public void testAdded() throws Exception {

        final Map<String, Map<String, Object>> localData = new HashMap<>();

        final WebSocketClient client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());

        final ObjectConverter converter = new ObjectConverterJson();

        final MapSubscriptionAdapter subscriptionAdapter = new MapSubscriptionAdapter(client, converter, localData);

        client.registerHandler(new Object() {

            @MessageHandler
            public void handleConnected(final ConnectedMessage message) throws IOException {

                subscriptionAdapter.subscribe("tabs", null, new SubscriptionCallback() {
                    @Override
                    public void onReady(String subscriptionId) {

                        Assert.assertEquals(1, subscriptionAdapter.getDataMap().size());

                        Assert.assertTrue(subscriptionAdapter.getDataMap().keySet().contains("tabs"));
                        client.disconnect();

                    }

                    @Override
                    public void onFailure(String subscriptionId, DDPError error) {
                        Assert.fail();
                    }
                });
            }
        });

        client.connect(Constants.SERVER_ADDRESS);
    }

    @Test
    public void testTypedAdded() throws Exception {

        final Map<String, Map<String, Object>> localData = new HashMap<>();

        final ObjectConverter converter = new ObjectConverterJson();

        final WebSocketClient client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());

        final MapSubscriptionAdapter subscriptionAdapter = new MapSubscriptionAdapter(client, converter, localData);

        client.registerHandler(new Object() {

            @MessageHandler
            public void handleConnected(ConnectedMessage message) throws IOException {

                subscriptionAdapter.subscribe("tabs", null, Constants.Tab.class, new SubscriptionCallback() {
                    @Override
                    public void onReady(String subscriptionId) {

                        Assert.assertEquals(1, subscriptionAdapter.getDataMap().size());

                        Assert.assertTrue(subscriptionAdapter.getDataMap().keySet().contains("tabs"));
                        client.disconnect();

                    }

                    @Override
                    public void onFailure(String subscriptionId, DDPError error) {
                        Assert.fail();
                    }
                });
            }
        });

        client.connect(Constants.SERVER_ADDRESS);
    }

    @Test
    public void testRemoved() throws Exception {

        final Map<String, Map<String, Object>> localData = new HashMap<>();

        final ObjectConverter converter = new ObjectConverterJson();

        final WebSocketClient client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());

        final MapSubscriptionAdapter subscriptionAdapter = new MapSubscriptionAdapter(client, converter, localData);


        //subscriptionAdapter.handleRemoved();


        client.registerHandler(new Object() {

            @MessageHandler
            private void handleConnected(ConnectedMessage message) throws IOException {

                subscriptionAdapter.subscribe("tabs", null, new SubscriptionCallback() {
                    @Override
                    public void onReady(String subscriptionId) {

                        Assert.assertEquals(1, subscriptionAdapter.getDataMap().size());

                        Assert.assertTrue(subscriptionAdapter.getDataMap().keySet().contains("tabs"));
                        client.disconnect();

                    }

                    @Override
                    public void onFailure(String subscriptionId, DDPError error) {
                        Assert.fail();
                    }
                });
            }
        });

        client.connect(Constants.SERVER_ADDRESS);
    }

}
