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
 * Thrown when we get a message from the server that we don't know how to process.
 *
 * @author geoffc@gmail.com
 * @since 1/17/14 at 7:15 PM.
 */
public class UnsupportedMessageException extends Exception {
    public UnsupportedMessageException(String rawMessage) {
        super(rawMessage);
    }
}
