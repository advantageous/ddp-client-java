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

public class SubscriptionAdapterTest {

    @Test
    public void testSubscriptionAndReady() throws Exception {


        final WebSocketClient endpoint = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());
        final ObjectConverter converter = new ObjectConverterJson();

        final SubscriptionAdapter subscriptionAdapter = new BaseSubscriptionAdapter(endpoint, converter);

        endpoint.registerHandler(new Object() {

            @MessageHandler
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

            @MessageHandler
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

            @MessageHandler
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
