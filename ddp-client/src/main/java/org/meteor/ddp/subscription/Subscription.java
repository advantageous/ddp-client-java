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
 * This object represents a subscription to a meteor collection.
 *
 * @author geoffc@gmail.com
 * @since 2/15/14 at 2:58 PM.
 */
public class Subscription {

    private final String subscriptionName;

    private final Object[] params;

    private final Class clazz;

    private final Callback callback;

    public Subscription(String subscriptionName, Object[] params, Class clazz, Callback callback) {
        this.subscriptionName = subscriptionName;
        this.params = params;
        this.clazz = clazz;
        this.callback = callback;
    }

    public Subscription(String subscriptionName) {
        this(subscriptionName, null, Object.class, null);
    }

    public Subscription(String subscriptionName, Object[] params) {
        this(subscriptionName, params, Object.class, null);
    }

    public Subscription(String subscriptionName, Class clazz) {
        this(subscriptionName, null, clazz, null);
    }

    public Subscription(String subscriptionName, Object[] params, Class clazz) {
        this(subscriptionName, params, clazz, null);
    }

    public Subscription(String subscriptionName, Class clazz, Callback callback) {
        this(subscriptionName, null, clazz, callback);
    }

    public Subscription(String subscriptionName, Object[] params, Callback callback) {
        this(subscriptionName, params, Object.class, callback);
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public Object[] getParams() {
        return params;
    }

    public Class getClazz() {
        return clazz;
    }

    public Callback getCallback() {
        return callback;
    }

    /**
     * Callback run when a subscription is handled.
     */
    public interface Callback {

        void onReady(String subscriptionId);

        void onFailure(String subscriptionId, DDPError error);
    }
}
