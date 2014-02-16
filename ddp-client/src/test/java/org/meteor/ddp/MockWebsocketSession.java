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

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * description
 *
 * @author geoffc@gmail.com
 * @since 2/15/14 at 8:47 PM.
 */
public class MockWebsocketSession implements Session {

    Set<MessageHandler> handlers = new HashSet<>();

    @Override
    public WebSocketContainer getContainer() {
        return null;
    }

    @Override
    public void addMessageHandler(MessageHandler handler) throws IllegalStateException {
        handlers.add(handler);
    }

    @Override
    public Set<MessageHandler> getMessageHandlers() {
        return handlers;
    }

    @Override
    public void removeMessageHandler(MessageHandler handler) {
        handlers.remove(handler);
    }

    @Override
    public String getProtocolVersion() {
        return "13";
    }

    @Override
    public String getNegotiatedSubprotocol() {
        return null; //TODO: Implement.
    }

    @Override
    public List<Extension> getNegotiatedExtensions() {
        return null; //TODO: Implement.
    }

    @Override
    public boolean isSecure() {
        return false; //TODO: Implement.
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public long getMaxIdleTimeout() {
        return 0; //TODO: Implement.
    }

    @Override
    public void setMaxIdleTimeout(long milliseconds) {
        //TODO: Implement.
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        return 0; //TODO: Implement.
    }

    @Override
    public void setMaxBinaryMessageBufferSize(int length) {
        //TODO: Implement.
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        return 0; //TODO: Implement.
    }

    @Override
    public void setMaxTextMessageBufferSize(int length) {
        //TODO: Implement.
    }

    @Override
    public RemoteEndpoint.Async getAsyncRemote() {
        return null; //TODO: Implement.
    }

    @Override
    public RemoteEndpoint.Basic getBasicRemote() {
        return null; //TODO: Implement.
    }

    @Override
    public String getId() {
        return null; //TODO: Implement.
    }

    @Override
    public void close() throws IOException {
        //TODO: Implement.
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        //TODO: Implement.
    }

    @Override
    public URI getRequestURI() {
        return null; //TODO: Implement.
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        return null; //TODO: Implement.
    }

    @Override
    public String getQueryString() {
        return null; //TODO: Implement.
    }

    @Override
    public Map<String, String> getPathParameters() {
        return null; //TODO: Implement.
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return null; //TODO: Implement.
    }

    @Override
    public Principal getUserPrincipal() {
        return null; //TODO: Implement.
    }

    @Override
    public Set<Session> getOpenSessions() {
        return null; //TODO: Implement.
    }
}
