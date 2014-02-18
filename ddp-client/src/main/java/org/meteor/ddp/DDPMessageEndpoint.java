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

import java.io.IOException;

/**
 * This is the core websocket client for the DDP communication.  This endpoint is responsible for handling DDP messages
 * and dispatching registered callbacks to listeners.
 *
 * @author geoffc@gmail.com
 * @since 2/17/14 at 10:52 PM.
 */
public interface DDPMessageEndpoint {
    void disconnect() throws IOException;

    void connect(String address) throws IOException, InterruptedException;

    <T> void registerHandler(Class<T> messageType,
                             DDPMessageHandler.Phase phase, DDPMessageHandler<T> handler);

    <T> void registerHandler(Class<T> messageType,
                             DDPMessageHandler<T> handler);

    void await() throws InterruptedException;

    void send(Object message) throws IOException;

}
