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
import org.meteor.ddp.MessageConverter;
import org.meteor.ddp.MessageConverterJson;
import org.meteor.ddp.subscription.MapSubscriptionAdapter;
import org.meteor.ddp.subscription.ObjectConverter;
import org.meteor.ddp.subscription.ObjectConverterJson;
import org.meteor.ddp.subscription.Subscription;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.websocket.WebSocketContainer;
import java.util.*;

/**
 * Guice module for this sample application.
 *
 * @author geoffc@gmail.com
 * @since 2/11/14 at 12:53 PM.
 */
public class SampleApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    WebSocketContainer provideWebSocketContainer() {
        return ClientManager.createClient();
    }

    @Provides
    @Singleton
    MessageConverter provideMessageConverter() {
        return new MessageConverterJson();
    }

    @Provides
    @Singleton
    SubscriptionEventDispatcher provideDispatcher(final EventBus eventBus,
                                                  final DDPMessageEndpoint endpoint,
                                                  final @Named("Local Data Map") Map<String, Map<String, Object>> dataMap) {
        return new SubscriptionEventDispatcher(eventBus, endpoint, dataMap);
    }

    @Provides
    @Singleton
    DDPMessageEndpoint provideWebSocketClient(final WebSocketContainer container,
                                              final MessageConverter converter) {
        return new DDPMessageEndpoint(container, converter);
    }

    @Provides
    @Singleton
    @Named("Subscriptions")
    Set<Subscription> provideSubscriptions() {
        return new HashSet<>(Arrays.asList(new Subscription[]{
                new Subscription(WebApplicationConstants.TABS_COLLECTION_NAME, Tab.class)
        }));
    }

    @Provides
    @Singleton
    ObjectConverter provideObjectConverter() {
        return new ObjectConverterJson();
    }

    @Provides
    @Singleton
    @Named("Local Data Map")
    Map<String, Map<String, Object>> provideDataMap() {
        return new HashMap<>();
    }

    @Provides
    @Singleton
    MapSubscriptionAdapter provideMapSubscriptionAdapter(final DDPMessageEndpoint client,
                                                         final ObjectConverter objectConverter,
                                                         final @Named("Subscriptions") Set<Subscription> subscriptions,
                                                         final @Named("Local Data Map") Map<String, Map<String, Object>> dataMap) {
        return new MapSubscriptionAdapter(client, subscriptions, objectConverter, dataMap);
    }

}
