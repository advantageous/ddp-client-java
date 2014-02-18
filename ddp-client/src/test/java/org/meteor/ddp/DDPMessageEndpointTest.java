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

package org.meteor.ddp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DDPMessageEndpointTest {

    public static final String WS_URL = "ws://example.com/websocket";

    private WebSocketContainer wsContainer;

    @Before
    public void setup() {
        this.wsContainer = mock(WebSocketContainer.class);
    }

    @Test
    public void testRegisterHandler() throws Exception {
        DDPMessageEndpointImpl client = new DDPMessageEndpointImpl(wsContainer, null);
        client.registerHandler(ConnectedMessage.class, message -> {
        });
        List<DDPMessageEndpointImpl.InstanceMethodContainer> methods = client.handlerMap.get(ConnectedMessage.class);
        assertEquals(2, methods.size());
    }

    /**
     * TODO: Make this test actually mock the Session and verify that the connection happens
     *
     * @throws Exception
     */
    @Test
    public void testConnectAndDisconnect() throws Exception {

        final DDPMessageEndpoint client = new DDPMessageEndpointImpl(wsContainer, new JsonMessageConverter());
        client.registerHandler(ConnectedMessage.class, message -> {
        });

        client.connect(WS_URL);
        ArgumentCaptor<URI> arg = ArgumentCaptor.forClass(URI.class);

        verify(this.wsContainer).connectToServer(any(DDPMessageEndpointImpl.class), any(ClientEndpointConfig.class), arg.capture());

        Assert.assertEquals(new URI(WS_URL), arg.getValue());


    }

}
