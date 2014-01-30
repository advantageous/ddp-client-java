package org.meteor.sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.meteor.ddp.MessageHandler;
import org.meteor.ddp.subscription.AddedMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class SampleController implements Initializable {

    @FXML
    private TextArea messages;

    @FXML
    private TableView<RowData> table;

    @FXML
    private TableColumn<RowData, String> column;

    private void log(String text) {
        messages.appendText(text + "\n");
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
    }

}