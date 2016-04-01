package com.accounted4.assetmanager.util.vaadin.ui;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.BrowserFrame;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;

/**
 * A Panel to slide in from the right side of the layout when the exposed tab is clicked.
 * The panel displays an html resource with help for using the application panel on display.
 *
 * @author gheinze
 */
public class HelpSlider {


    public static SliderPanel create(String helpPageName) {

        ThemeResource helpPage = new ThemeResource("../a4am/helpPages/" + helpPageName);
        BrowserFrame browserFrame = new BrowserFrame("help", helpPage);
        browserFrame.setSizeFull();

        SliderPanel rightSlider =
            new SliderPanelBuilder(browserFrame, "Help")
                .mode(SliderMode.RIGHT)
                .tabPosition(SliderTabPosition.MIDDLE)
                .flowInContent(true)
                .autoCollapseSlider(true)
                .style(SliderPanelStyles.COLOR_GRAY)
                .animationDuration(100)
                .fixedContentSize(600)
                .build();

        return rightSlider;
    }

}
