package com.smarttab.pos;

import javafx.scene.text.Text;

public class RowData {

    private Text message;

    public RowData(Text message) {
        this.message = message;
        message.setWrappingWidth(400);
    }

    public Text getMessage() {
        return message;
    }

    public void setMessage(Text message) {
        this.message = message;
    }
}