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

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.meteor.ddp.DDPMessageEndpoint;
import org.meteor.ddp.ErrorMessage;
import org.meteor.ddp.OnMessage;
import org.meteor.ddp.subscription.MapSubscriptionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class SampleApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleApplication.class);

    @Inject
    private DDPMessageEndpoint client;

    @Inject
    private MainViewController mainController;

    @Inject
    private SubscriptionEventDispatcher dispatcher;

    @Inject
    private MapSubscriptionAdapter mapSubscriptionAdapter;

    {
        final Injector injector = Guice.createInjector(new SampleApplicationModule());

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> aClass) {
                return injector.getInstance(aClass);
            }
        });
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        injector.injectMembers(this);

        client.registerHandler(this);
        client.registerHandler(dispatcher);
        client.registerHandler(mapSubscriptionAdapter);

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        MeteorService thread = new MeteorService();
        thread.start();

        Parent root = mainController.getRoot();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @OnMessage
    public void handleError(ErrorMessage message) {
        LOGGER.error("error: " + message.getReason());
    }

    class MeteorService extends Service {

        @Override
        protected Task createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    client.connect("ws://localhost:3000/websocket");
                    client.await();
                    LOGGER.warn("disconected from endpoint");
                    return null;
                }
            };
        }
    }
}
