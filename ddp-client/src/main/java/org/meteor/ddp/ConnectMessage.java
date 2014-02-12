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

/**
 * Message sent from client to server to establish a DDP connection.
 *
 * @author geoffc@gmail.com
 * @since 1/17/14 at 6:36 PM.
 */
public class ConnectMessage {

    private String session;

    private String version;

    private String[] support;

    public ConnectMessage() {
    }

    public ConnectMessage(String version, String[] support) {
        this.version = version;
        this.support = support;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getSupport() {
        return support;
    }

    public void setSupport(String[] support) {
        this.support = support;
    }
}
