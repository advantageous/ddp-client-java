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
        final ObjectConverter converter = new ObjectConverterJson();

        final SubscriptionAdapter subscriptionAdapter = new BaseSubscriptionAdapter(endpoint, converter);

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

        endpoint.connect(Constants.SERVER_ADDRESS);
    }

    @Test
    public void testTypedSubscriptionAndReady() throws Exception {

        MessageConverterJson messageConverter = new MessageConverterJson();

        final WebSocketClient endpoint = new WebSocketClient(ClientManager.createClient(), messageConverter);
        final ObjectConverter converter = new ObjectConverterJson();

        final SubscriptionAdapter subscriptionAdapter = new BaseSubscriptionAdapter(endpoint, converter);

        endpoint.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
            public void handleConnected(ConnectedMessage message) throws IOException {

                subscriptionAdapter.subscribe("tabs", null, Constants.Tab.class, new SubscriptionCallback() {
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

        endpoint.connect(Constants.SERVER_ADDRESS);
    }

    @Test
    public void testFailedSubscription() throws Exception {

        final WebSocketClient endpoint = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());
        final ObjectConverter converter = new ObjectConverterJson();

        final SubscriptionAdapter subscriptionAdapter = new BaseSubscriptionAdapter(endpoint, converter);

        endpoint.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
            public void handleConnected(ConnectedMessage message) throws IOException {

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

        endpoint.connect(Constants.SERVER_ADDRESS);
    }
}
