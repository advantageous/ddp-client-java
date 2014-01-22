package org.meteor.ddp.rpc;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Assert;
import org.junit.Test;
import org.meteor.ddp.*;

import java.io.IOException;
import java.util.Date;

public class RPCClientTest {

    final WebSocketClient client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());

    @Test
    public void testCall() throws Exception {

        final RPCClient rpcClient = new RPCClientImpl(client);

        client.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
            private void handleConnected(ConnectedMessage message) throws IOException {

                MyTestClass one = new MyTestClass();
                one.setName("First Object");
                MyTestClass two = new MyTestClass();
                two.setName("Second Object");
                two.setDate(new Date());
                MyTestClass three = new MyTestClass();
                three.setDate(new Date());

                rpcClient.call("addTab", new Object[]{one, two, three}, new AsyncCallback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        client.disconnect();
                    }

                    @Override
                    public void onFailure(DDPError message) {
                        Assert.fail(message.getReason());
                    }
                });
            }
        });

        client.connect("ws://192.168.1.110:3200/websocket");

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