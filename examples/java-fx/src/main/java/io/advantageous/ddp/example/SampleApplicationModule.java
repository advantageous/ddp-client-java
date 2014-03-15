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

package io.advantageous.ddp.example;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.DDPMessageEndpointImpl;
import io.advantageous.ddp.JsonMessageConverter;
import io.advantageous.ddp.MessageConverter;
import io.advantageous.ddp.subscription.JsonObjectConverter;
import io.advantageous.ddp.subscription.MapSubscriptionAdapter;
import io.advantageous.ddp.subscription.ObjectConverter;
import io.advantageous.ddp.subscription.SubscriptionAdapter;
import javafx.fxml.FXMLLoader;
import org.glassfish.tyrus.client.ClientManager;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
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
        bind(SampleApplication.class);

        bind(MessageConverter.class).to(JsonMessageConverter.class).in(Singleton.class);
        bind(ObjectConverter.class).to(JsonObjectConverter.class).in(Singleton.class);
        bind(SubscriptionAdapter.class).to(MapSubscriptionAdapter.class).in(Singleton.class);
        bind(DDPMessageEndpoint.class).to(DDPMessageEndpointImpl.class).in(Singleton.class);
        bind(SubscriptionEventDispatcher.class).asEagerSingleton();
        bind(EventBus.class).in(Singleton.class);

        bindListener(
                new AbstractMatcher<TypeLiteral<?>>() {
                    @Override
                    public boolean matches(TypeLiteral<?> typeLiteral) {
                        return typeLiteral.getRawType().isAnnotationPresent(Presents.class);
                    }
                },
                new TypeListener() {
                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                        final Presents presents = type.getRawType().getAnnotation(Presents.class);
                        encounter.register((InjectionListener<I>) new InjectionListener<I>() {
                            @Override
                            public void afterInjection(I injectee) {
                                final FXMLLoader loader = new FXMLLoader(injectee.getClass().getResource(presents.value()));
                                loader.setController(injectee);
                                try {
                                    loader.load();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                }
        );
    }

    @Provides
    @Singleton
    WebSocketContainer provideContainer() {
        return ClientManager.createClient();
    }

    /**
     * In this example we just use a new HashMap, but the intention here is you could have a map provider by something
     * like Hazelcast, JGroups, Memcached, etc.
     *
     * @return the map interface for your local data
     */
    @Provides
    @Singleton
    @Named("Local Data Map")
    Map<String, Map<String, Object>> provideDataMap() {
        return new HashMap<>();
    }

}
