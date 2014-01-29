package org.meteor.ddp.subscription;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Assert;
import org.junit.Test;
import org.meteor.ddp.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapSubscriptionAdapterTest {

    public static final String SERVER_ADDRESS = "ws://192.168.1.104:3200/websocket";

    @Test
    public void testAdded() throws Exception {

        final Map<String, Map<String, Object>> localData = new HashMap<>();

        final WebSocketClient client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());

        final MapSubscriptionAdapter subscriptionAdapter = new MapSubscriptionAdapter(client, localData);

        client.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
            public void handleConnected(ConnectedMessage message) throws IOException {

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

        client.connect(SERVER_ADDRESS);
    }

    @Test
    public void testRemoved() throws Exception {

        final Map<String, Map<String, Object>> localData = new HashMap<>();

        final WebSocketClient client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());

        final MapSubscriptionAdapter subscriptionAdapter = new MapSubscriptionAdapter(client, localData);


        //subscriptionAdapter.handleRemoved();


        client.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
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

        client.connect(SERVER_ADDRESS);
    }

}
