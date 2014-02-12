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
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class MainViewController {

    @FXML
    private VBox scrollingVBox;

    @FXML
    private Label status;

    @FXML
    private AnchorPane root;

    @Inject
    public MainViewController(final EventBus eventBus) {
        eventBus.register(this);
    }

    @Subscribe
    public void handleAdded(final TabAddedEvent event) {
        scrollingVBox.getChildren().add(new TabView(event.getTab()));
    }

    public AnchorPane getRoot() {
        return root;
    }

    public Label getStatus() {
        return status;
    }
}
