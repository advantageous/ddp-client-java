package com.smarttab.pos;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.glassfish.tyrus.client.ClientManager;
import org.meteor.ddp.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SampleController implements Initializable {

    WebSocketClient client = new WebSocketClient(ClientManager.createClient(), new MessageConverterJson());

    @FXML
    private TableView<RowData> table;

    @FXML
    private TableColumn<RowData, String> column;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        MeteorService thread = new MeteorService();
        thread.start();
    }

    @MessageHandler(ConnectedMessage.class)
    private void handleConnected(ConnectedMessage message) {
        addTextToList("connected: " + message.getSession());
    }

    @MessageHandler(ErrorMessage.class)
    private void handleError(ErrorMessage message) {
        addTextToList("error: " + message.getReason());
    }

    private void addTextToList(final String text) {
        table.getItems().add(0, new RowData(new Text(text)));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table.setEditable(true);
        column.setResizable(true);
        column.setCellValueFactory(new PropertyValueFactory<RowData, String>("message"));
    }

    class MeteorService extends Service {

        @Override
        protected Task createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    client.registerHandler(SampleController.this);
                    client.connect("ws://192.168.1.110:3200/websocket");
                    return null;
                }
            };
        }
    }
}