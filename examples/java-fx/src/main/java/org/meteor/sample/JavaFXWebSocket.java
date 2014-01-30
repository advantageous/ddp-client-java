package org.meteor.sample;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.glassfish.tyrus.client.ClientManager;
import org.meteor.ddp.*;
import org.meteor.ddp.subscription.MapSubscriptionAdapter;
import org.meteor.ddp.subscription.ObjectConverterJson;
import org.meteor.ddp.subscription.SubscriptionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JavaFXWebSocket extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaFXWebSocket.class);

    final Map<String, Map<String, Object>> localData = new HashMap<>();

    final MapSubscriptionAdapter subscriptionAdapter;

    final WebSocketClient client;

    {
        client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());
        client.registerHandler(this);
        subscriptionAdapter = new MapSubscriptionAdapter(client, new ObjectConverterJson(), localData);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();

        MeteorService thread = new MeteorService();
        thread.start();
    }

    @MessageHandler(ConnectedMessage.class)
    public void handleConnected(ConnectedMessage message) throws IOException {
        LOGGER.info("connected: " + message.getSession());
        subscriptionAdapter.subscribe("tabs", null, new SubscriptionCallback() {
            @Override
            public void onReady(String subscriptionId) {
                LOGGER.info("subscription ready: " + subscriptionId);
            }

            @Override
            public void onFailure(String subscriptionId, DDPError error) {
                LOGGER.error("subscription failed: " + error);
            }
        });
    }

    @MessageHandler(ErrorMessage.class)
    public void handleError(ErrorMessage message) {
        LOGGER.error("error: " + message.getReason());
    }

    class MeteorService extends Service {

        @Override
        protected Task createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    LOGGER.info("connecting to client.");
                    client.connect("ws://localhost:3000/websocket");
                    return null;
                }
            };
        }
    }
}