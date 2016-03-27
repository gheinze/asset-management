package com.accounted4.assetmanager;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;

/**
 * A Layout for the Application:
 *
 * <pre>
 * +--------------------+
 * | Header             |
 * +--------------------+
 * | Menu | Content     |
 * |      |             |
 * +--------------------+
 * | Footer             |
 * +--------------------+
 * </pre>
 *
 *   o A "Header" area with a logo which pops up an "about" dialog
 *   o The "Main" area which contains
 *       o a menu on the left
 *       o the content of selected menu item on the right
 *   o A "Footer" area
 * @author gheinze
 */
@SpringComponent
@UIScope
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApplicationLayout extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private static final String HEADER_HEIGHT = "32px";


    private final ApplicationContentArea viewContainer;
    private final ApplicationMenu applicationMenu;
    private final AboutContent aboutContent;


    @PostConstruct
    public void init() {
        addHeader();
        addMain();
        //addFooter();
    }


    private void addHeader() {

        HorizontalLayout header  = new HorizontalLayout();
        header.setWidth("100%");
        header.setHeight(HEADER_HEIGHT);

        Label initialSpacer = new Label(" ");
        initialSpacer.setWidth("10px");
        initialSpacer.setHeight(HEADER_HEIGHT);
        header.addComponent(initialSpacer);

        aboutContent.getAboutPopupView().setHeight(HEADER_HEIGHT);
        aboutContent.getAboutPopupView().setWidth("10px");
        header.addComponents(aboutContent.getInvokingButton(), aboutContent.getAboutPopupView());
        header.setStyleName("appHeader");
        header.setComponentAlignment(aboutContent.getInvokingButton(), Alignment.MIDDLE_LEFT);

        Label extraSpaceAbsorber = new Label(" ");
        extraSpaceAbsorber.setHeight(HEADER_HEIGHT);
        header.addComponent(extraSpaceAbsorber);
        header.setExpandRatio(extraSpaceAbsorber, 1.0f);

        header.addComponent(new Button("Glenn"));

        Panel headerPanel = new Panel();
        headerPanel.setHeightUndefined();
        headerPanel.setWidth(100, Unit.PERCENTAGE);
        headerPanel.setContent(header);

        addComponent(headerPanel);

    }


    private void addMain() {
        HorizontalSplitPanel mainArea = new HorizontalSplitPanel();
        mainArea.setFirstComponent(applicationMenu);
        mainArea.setSecondComponent(viewContainer);
        mainArea.setSplitPosition(15, Unit.PERCENTAGE);
        mainArea.setSizeFull();
        mainArea.setHeight("100%");
        addComponent(mainArea);
        setExpandRatio(mainArea, 1.0f);
    }

//    private void addFooter() {
//        HorizontalLayout footer  = new HorizontalLayout();
//        addComponent(footer);
//    }



}
