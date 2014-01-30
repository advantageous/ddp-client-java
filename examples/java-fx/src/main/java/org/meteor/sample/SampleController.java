package org.meteor.sample;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.glassfish.tyrus.client.ClientManager;
import org.meteor.ddp.*;
import org.meteor.ddp.subscription.AddedMessage;
import org.meteor.ddp.subscription.MapSubscriptionAdapter;
import org.meteor.ddp.subscription.ObjectConverterJson;
import org.meteor.ddp.subscription.SubscriptionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SampleController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleController.class);

    final Map<String, Map<String, Object>> localData = new HashMap<>();

    final MapSubscriptionAdapter subscriptionAdapter;

    final WebSocketClient client;

    {
        client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());
        client.registerHandler(this);

        subscriptionAdapter = new MapSubscriptionAdapter(client, new ObjectConverterJson(), localData);
    }

    @FXML
    private TextArea messages;

    @FXML
    private TableView<RowData> table;

    @FXML
    private TableColumn<RowData, String> column;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

    }

    @MessageHandler(ConnectedMessage.class)
    public void handleConnected(ConnectedMessage message) throws IOException {
        log("connected: " + message.getSession());
        subscriptionAdapter.subscribe("tabs", null, new SubscriptionCallback() {
            @Override
            public void onReady(String subscriptionId) {
                log("subscription ready");
            }

            @Override
            public void onFailure(String subscriptionId, DDPError error) {
                log("subscription failed");
            }
        });
    }

    private void log(String text){
        messages.appendText(text + "\n");
    }

    @MessageHandler(ErrorMessage.class)
    public void handleError(ErrorMessage message) {
        log("error: " + message.getReason());
    }

    @MessageHandler(AddedMessage.class)
    public void handleAdded(AddedMessage message) {
        log("item added to collection: " + message.getCollection());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table.setEditable(true);
        column.setResizable(true);
        column.setCellValueFactory(new PropertyValueFactory<RowData, String>("message"));

        MeteorService thread = new MeteorService();
        thread.start();
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