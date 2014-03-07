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

package io.advantageous.ddp;

/**
 * Message sent from the server indicating an error in a previous message.
 *
 * @author geoffc@gmail.com
 * @since 1/17/14 at 6:36 PM.
 */

public class ErrorMessage {

    private String reason;

    private Object offendingMessage;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getOffendingMessage() {
        return offendingMessage;
    }

    public void setOffendingMessage(Object offendingMessage) {
        this.offendingMessage = offendingMessage;
    }

    @Override
    public String toString() {
        return "Error{" +
                "reason='" + reason + '\'' +
                ", offendingMessage=" + offendingMessage +
                '}';
    }
}
