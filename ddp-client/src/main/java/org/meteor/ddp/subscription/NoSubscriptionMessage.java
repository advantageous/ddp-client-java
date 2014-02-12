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

package org.meteor.ddp.subscription;

import org.meteor.ddp.DDPError;

/**
 * No Subscription Message
 *
 * @author geoffc@gmail.com
 * @since 1/17/14 at 6:10 PM.
 */

public class NoSubscriptionMessage {

    private String id;

    private DDPError error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DDPError getError() {
        return error;
    }

    public void setError(DDPError error) {
        this.error = error;
    }
}
