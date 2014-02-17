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

package org.meteor.ddp.rpc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.meteor.ddp.ConnectedMessage;
import org.meteor.ddp.DDPError;
import org.meteor.ddp.DDPMessageEndpoint;
import org.meteor.ddp.MessageConverterJson;

import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.util.Date;

import static org.mockito.Mockito.mock;

public class RPCClientTest {


    private WebSocketContainer wsContainer;

    @Before
    public void setup() {
        this.wsContainer = mock(WebSocketContainer.class);
    }

    @Test
    public void testCall() throws Exception {

        DDPMessageEndpoint endpoint = new DDPMessageEndpoint(this.wsContainer, new MessageConverterJson());

        final RPCClient rpcClient = new RPCClientImpl(endpoint);

        endpoint.registerHandler(ConnectedMessage.class, message -> {

            MyTestClass one = new MyTestClass();
            one.setName("First Object");
            MyTestClass two = new MyTestClass();
            two.setName("Second Object");
            two.setDate(new Date());
            MyTestClass three = new MyTestClass();
            three.setDate(new Date());

            try {
                rpcClient.call("addTab", new Object[]{one, two, three}, new AsyncCallback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                    }

                    @Override
                    public void onFailure(DDPError message) {
                        Assert.fail(message.getReason());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        });

        endpoint.connect("ws://example.com/websocket");

    }

    class MyTestClass {

        private String name;

        private Date date;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}