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

import com.google.inject.Guice;
import io.advantageous.ddp.ConnectedMessage;
import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.ErrorMessage;
import io.advantageous.ddp.subscription.Subscription;
import io.advantageous.ddp.subscription.SubscriptionAdapter;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class SampleApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleApplication.class);

    @Inject
    private DDPMessageEndpoint endpoint;

    @Inject
    private MainViewController mainController;

    @Inject
    private SubscriptionAdapter adapter;

    {
        Guice.createInjector(new SampleApplicationModule()).injectMembers(this);
        endpoint.registerHandler(ErrorMessage.class, message -> LOGGER.error(message.getReason()));
        endpoint.registerHandler(ConnectedMessage.class, message -> {
            try {
                adapter.subscribe(new Subscription(WebApplicationConstants.TABS_COLLECTION_NAME, Tab.class)
                );
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        new MeteorService().start();
        stage.setScene(new Scene(mainController.getRoot()));
        stage.show();
    }

    class MeteorService extends Service {
        @Override
        protected Task createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    endpoint.connect("ws://localhost:3000/websocket");
                    endpoint.await();
                    LOGGER.warn("disconnected from endpoint");
                    return null;
                }
            };
        }
    }
}
