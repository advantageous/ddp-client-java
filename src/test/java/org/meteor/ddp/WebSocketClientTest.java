package org.meteor.ddp;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class WebSocketClientTest {

    @Test
    public void testRegisterHandler() throws Exception {
        WebSocketClient client = new WebSocketClient(ClientManager.createClient(), null);
        client.registerHandler(new Object() {
            @MessageHandler(ConnectedMessage.class)
            public void handleReady(ConnectedMessage message) {
            }
        });
        Set<WebSocketClient.InstanceMethodContainer> methods = client.handlerMap.get(ConnectedMessage.class);
        Assert.assertEquals(1, methods.size());
    }

    @Test
    public void testConnectAndDisconnect() throws Exception {

        final WebSocketClient endpoint = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());
        endpoint.registerHandler(new Object() {

            @MessageHandler(ConnectedMessage.class)
            private void handleConnected(ConnectedMessage message) {
                endpoint.disconnect();
            }
        });

        endpoint.connect("ws://192.168.1.110:3200/websocket");
    }

}
