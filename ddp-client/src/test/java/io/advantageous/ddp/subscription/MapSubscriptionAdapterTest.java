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

package io.advantageous.ddp.subscription;

import io.advantageous.ddp.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import io.advantageous.ddp.subscription.message.AddedMessage;
import org.mockito.ArgumentCaptor;

import javax.websocket.*;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MapSubscriptionAdapterTest {

    private static final String CONNECTED_MESSAGE = "{\"msg\":\"connected\",\"session\":\"DNLpTnL8ZPTTizPvC\"}";

    private static final String[] ADDED_MESSAGES = {
            "{\"msg\":\"added\",\"collection\":\"tabs\",\"id\":\"uA6nsqCHnmjT3xmsm\",\"fields\":{\"name\":\"Ian Serlin\",\"total\":45.00}}",
            "{\"msg\":\"added\",\"collection\":\"tabs\",\"id\":\"GkXcKNamHLesd57wi\",\"fields\":{\"name\":\"Geoff Chandler\",\"total\":50.00}}",
            "{\"msg\":\"added\",\"collection\":\"tabs\",\"id\":\"odje77pej68MiSdPB\",\"fields\":{\"name\":\"Daniel Baron\",\"total\":255.00}}",
            "{\"msg\":\"added\",\"collection\":\"tabs\",\"id\":\"uWz3rCXYAuevqX2bJ\",\"fields\":{\"name\":\"Foo\",\"total\":5}}"
    };

    private WebSocketContainer wsContainer;

    private Session mockSession;

    @Before
    public void setup() throws Exception {
        this.wsContainer = mock(WebSocketContainer.class);

        this.mockSession = mock(Session.class);
        when(mockSession.isOpen()).thenReturn(true);

        RemoteEndpoint.Async asyncRemote = mock(RemoteEndpoint.Async.class);
        when(mockSession.getAsyncRemote()).thenReturn(asyncRemote);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAdded() throws Exception {

        final Map<String, Map<String, Object>> localData = new HashMap<>();

        final DDPMessageEndpoint client = new DDPMessageEndpointImpl(wsContainer, new JsonMessageConverter());

        final ObjectConverter converter = new JsonObjectConverter();

        new MapSubscriptionAdapter(client, new Subscription[]{new Subscription("tabs")}, converter, localData);

        final Set<Object> results = new HashSet<>();

        client.registerHandler(AddedMessage.class, DDPMessageHandler.Phase.AFTER_UPDATE, message -> {
            results.add(localData.get(message.getCollection()).get(message.getId()));
        });

        client.connect("ws://example.com/websocket");

        ArgumentCaptor<DDPMessageEndpointImpl> endpointArgumentCaptor = ArgumentCaptor.forClass(DDPMessageEndpointImpl.class);

        when(wsContainer.connectToServer(any(Endpoint.class), any(ClientEndpointConfig.class), any(URI.class))).thenReturn(mockSession);

        verify(wsContainer).connectToServer(endpointArgumentCaptor.capture(), any(ClientEndpointConfig.class), any(URI.class));

        endpointArgumentCaptor.getValue().onOpen(mockSession, mock(EndpointConfig.class));

        ArgumentCaptor<MessageHandler.Whole> messageHandlerArgumentCaptor = ArgumentCaptor.forClass(MessageHandler.Whole.class);

        verify(mockSession).addMessageHandler(messageHandlerArgumentCaptor.capture());

        messageHandlerArgumentCaptor.getValue().onMessage(CONNECTED_MESSAGE);

        for (String message : ADDED_MESSAGES)
            messageHandlerArgumentCaptor.getValue().onMessage(message);

        Assert.assertEquals(4, results.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTypedAdded() throws Exception {

        final Map<String, Map<String, Object>> localData = new HashMap<>();

        final DDPMessageEndpoint client = new DDPMessageEndpointImpl(wsContainer, new JsonMessageConverter());

        final ObjectConverter converter = new JsonObjectConverter();

        new MapSubscriptionAdapter(client, new Subscription[]{new Subscription("tabs", Tab.class)}, converter, localData);

        final Set<Object> results = new HashSet<>();

        client.registerHandler(AddedMessage.class, DDPMessageHandler.Phase.AFTER_UPDATE, message -> {
            results.add(localData.get(message.getCollection()).get(message.getId()));
        });

        client.connect("ws://example.com/websocket");

        ArgumentCaptor<DDPMessageEndpointImpl> endpointArgumentCaptor = ArgumentCaptor.forClass(DDPMessageEndpointImpl.class);

        when(wsContainer.connectToServer(any(Endpoint.class), any(ClientEndpointConfig.class), any(URI.class))).thenReturn(mockSession);

        verify(wsContainer).connectToServer(endpointArgumentCaptor.capture(), any(ClientEndpointConfig.class), any(URI.class));

        endpointArgumentCaptor.getValue().onOpen(mockSession, mock(EndpointConfig.class));

        ArgumentCaptor<MessageHandler.Whole> messageHandlerArgumentCaptor = ArgumentCaptor.forClass(MessageHandler.Whole.class);

        verify(mockSession).addMessageHandler(messageHandlerArgumentCaptor.capture());

        messageHandlerArgumentCaptor.getValue().onMessage(CONNECTED_MESSAGE);

        for (String message : ADDED_MESSAGES)
            messageHandlerArgumentCaptor.getValue().onMessage(message);

        Assert.assertEquals(4, results.size());

    }

}
