package org.meteor.ddp.subscription;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Assert;
import org.junit.Test;
import org.meteor.ddp.*;

import java.io.IOException;

public class SubscriptionAdapterTest {

    @Test
    public void testSubscriptionAndReady() throws Exception {


        final WebSocketClient endpoint = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());
        final SubscriptionAdapter subscriptionAdapter = new BaseSubscriptionAdapter(endpoint);

        endpoint.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
            private void handleConnected(ConnectedMessage message) throws IOException {

                subscriptionAdapter.subscribe("tabs", null, new SubscriptionCallback() {
                    @Override
                    public void onReady(String subscriptionId) {
                        endpoint.disconnect();
                    }

                    @Override
                    public void onFailure(String subscriptionId, DDPError error) {
                        Assert.fail();
                    }
                });

            }
        });

        endpoint.connect("ws://192.168.1.110:3200/websocket");
    }

    @Test
    public void testFailedSubscription() throws Exception {

        final WebSocketClient endpoint = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());
        final SubscriptionAdapter subscriptionAdapter = new BaseSubscriptionAdapter(endpoint);

        endpoint.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
            private void handleConnected(ConnectedMessage message) throws IOException {

                subscriptionAdapter.subscribe("foo", null, new SubscriptionCallback() {
                    @Override
                    public void onReady(String subscriptionId) {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure(String subscriptionId, DDPError error) {
                        endpoint.disconnect();
                    }
                });

            }
        });

        endpoint.connect("ws://192.168.1.110:3200/websocket");
    }
}
