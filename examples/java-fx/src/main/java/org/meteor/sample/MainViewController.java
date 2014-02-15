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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Singleton
public class MainViewController implements Initializable {

    @FXML
    private VBox scrollingVBox;

    @FXML
    private AnchorPane root;

    @Inject
    private EventBus eventBus;

    private Map<String, TabView> itemMap = new HashMap<>();

    @Subscribe
    public void handleAdded(final TabAddedEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final TabView view = new TabView(event.getTab());
                itemMap.put(event.getKey(), view);
                scrollingVBox.getChildren().add(view);
            }
        });
    }

    @Subscribe
    public void handleRemoved(final TabRemovedEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final TabView node = itemMap.get(event.getKey());
                scrollingVBox.getChildren().remove(node);
                itemMap.remove(event.getKey());
            }
        });
    }

    @Subscribe
    public void handleModified(final TabUpdatedEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                itemMap.get(event.getKey()).setTab(event.getTab());
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventBus.register(this);
    }

    public AnchorPane getRoot() {
        return root;
    }

}
