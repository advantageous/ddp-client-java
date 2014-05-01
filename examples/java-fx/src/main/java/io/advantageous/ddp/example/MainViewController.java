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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.advantageous.ddp.repository.MeteorCollectionRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Singleton
@Presents("MainView.fxml")
public class MainViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainViewController.class);

    @FXML
    private VBox scrollingVBox;

    @FXML
    private AnchorPane root;

    @Inject
    private EventBus eventBus;

    @Inject
    private MeteorCollectionRepository meteorCollectionRepository;

    private Map<String, TabView> itemMap = new HashMap<>();

    @Subscribe
    public void handleAdded(final TabAddedEvent event) {
        Platform.runLater(() -> {
            final TabView view = new TabView(event.getTab(), mouseEvent -> {
                try {
                    this.meteorCollectionRepository.delete(
                            WebApplicationConstants.TABS_COLLECTION_NAME, event.getKey(),
                            result -> LOGGER.info("successfully deleted item: " + result),
                            message -> LOGGER.error("failed to delete item: " + message));
                } catch (final IOException e) {
                    LOGGER.error(e.getMessage());
                }
            });
            this.itemMap.put(event.getKey(), view);
            this.scrollingVBox.getChildren().add(view);
        });
    }

    @Subscribe
    public void handleRemoved(final TabRemovedEvent event) {
        Platform.runLater(() -> {
            final TabView node = itemMap.get(event.getKey());
            scrollingVBox.getChildren().remove(node);
            itemMap.remove(event.getKey());
        });
    }

    @Subscribe
    public void handleModified(final TabUpdatedEvent event) {
        Platform.runLater(() -> itemMap.get(event.getKey()).setTab(event.getTab()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventBus.register(this);
    }

    public AnchorPane getRoot() {
        return root;
    }

}
