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
import org.meteor.ddp.DDPMessageEndpoint;
import org.meteor.ddp.ErrorMessage;
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

    {
        final Injector injector = Guice.createInjector(new SampleApplicationModule());

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        loader.setControllerFactory(injector::getInstance);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        injector.injectMembers(this);

        endpoint.registerHandler(ErrorMessage.class, message -> LOGGER.error("error: " + message.getReason()));

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

    class MeteorService extends Service {

        @Override
        protected Task createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    endpoint.connect("ws://localhost:3000/websocket");
                    endpoint.await();
                    LOGGER.warn("disconected from endpoint");
                    return null;
                }
            };
        }
    }
}
