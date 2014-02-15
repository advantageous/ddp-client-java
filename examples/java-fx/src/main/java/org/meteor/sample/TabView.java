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

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Widget to display a tab item.
 *
 * @author geoffc@gmail.com
 * @since 2/1/14 at 9:22 PM.
 */
public class TabView extends HBox {

    private Tab tab;

    public TabView(Tab tab) {
        this.tab = tab;
        render();
    }

    public void setTab(Tab tab) {
        this.tab = tab;
        render();
    }

    public void render() {
        this.getChildren().clear();
        this.setSpacing(10d);
        this.getChildren().add(new Text(tab.getName()));
        this.getChildren().add(new Text("$" + tab.getTotal().toString()));
        if (tab.getCreatedAt() != null) this.getChildren().add(new Text(tab.getCreatedAt().toString()));
    }

}
