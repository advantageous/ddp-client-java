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

package org.meteor.sample;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.glassfish.tyrus.client.ClientManager;
import org.meteor.ddp.DDPMessageEndpoint;
import org.meteor.ddp.DDPMessageEndpointImpl;
import org.meteor.ddp.JsonMessageConverter;
import org.meteor.ddp.MessageConverter;
import org.meteor.ddp.subscription.*;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.websocket.WebSocketContainer;
import java.util.HashMap;
import java.util.Map;

/**
 * Guice module for this sample application.
 *
 * @author geoffc@gmail.com
 * @since 2/11/14 at 12:53 PM.
 */
public class SampleApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WebSocketContainer.class).toInstance(ClientManager.createClient());
        bind(MessageConverter.class).to(JsonMessageConverter.class).in(Singleton.class);
        bind(ObjectConverter.class).to(JsonObjectConverter.class).in(Singleton.class);
        bind(SubscriptionAdapter.class).to(MapSubscriptionAdapter.class).asEagerSingleton();
        bind(DDPMessageEndpoint.class).to(DDPMessageEndpointImpl.class).in(Singleton.class);
        bind(SubscriptionEventDispatcher.class).asEagerSingleton();
        bind(EventBus.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Named("Subscriptions")
    Subscription[] provideSubscriptions() {
        return new Subscription[]{
                new Subscription(WebApplicationConstants.TABS_COLLECTION_NAME, Tab.class)
        };
    }

    @Provides
    @Singleton
    @Named("Local Data Map")
    Map<String, Map<String, Object>> provideDataMap() {
        return new HashMap<>();
    }

}
