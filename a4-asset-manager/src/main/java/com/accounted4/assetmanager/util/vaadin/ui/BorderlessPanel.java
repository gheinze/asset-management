package com.accounted4.assetmanager.util.vaadin.ui;

import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author gheinze
 */
public class BorderlessPanel extends Panel {

    private BorderlessPanel() {
    }

    private BorderlessPanel(String caption) {
        super(caption);
    }

    public static BorderlessPanel create() {
        BorderlessPanel panel = new BorderlessPanel();
        panel.addStyleName(Reindeer.PANEL_LIGHT);
        return panel;
    }
    public static BorderlessPanel create(String caption) {
        BorderlessPanel panel = new BorderlessPanel(caption);
        panel.addStyleName(Reindeer.PANEL_LIGHT);  // remove border
        return panel;
    }


}
