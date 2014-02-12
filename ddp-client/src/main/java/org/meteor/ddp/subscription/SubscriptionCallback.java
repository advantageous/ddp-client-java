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
 * Callback run when a subscription is handled.
 *
 * @author geoffc@gmail.com
 * @since 1/21/14 at 4:08 PM.
 */
public interface SubscriptionCallback {

    void onReady(String subscriptionId);

    void onFailure(String subscriptionId, DDPError error);
}
