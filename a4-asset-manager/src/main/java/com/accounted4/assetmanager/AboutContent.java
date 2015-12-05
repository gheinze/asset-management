package com.accounted4.assetmanager;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

/**
 * An "About" popup window with a button to invoke it.
 *
 * @author gheinze
 */
@SpringComponent
@UIScope
public class AboutContent implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Getter private PopupView aboutPopupView;
    @Getter private Button invokingButton;

    @Value("${about.appName}") private String applicationName;
    @Value("${about.timestamp}") private String timestamp;
    @Value("${about.author}") private String author;


    @PostConstruct
    public void init() {
        aboutPopupView = new PopupView(null, getAboutPopupViewContent());
        invokingButton = getLogoButton();
        invokingButton.addClickListener(e -> {
            aboutPopupView.setPopupVisible(true);
        });
    }


    private Component getAboutPopupViewContent() {

        VerticalLayout aboutLayout = new VerticalLayout();
        aboutLayout.setHeight("500px");
        aboutLayout.setWidth("500px");
        aboutLayout.setStyleName("appAbout");

        String msg = String.format("<b>%s</b><br>v %s<br>%s", applicationName, timestamp, author);
        Label label = new Label(msg, ContentMode.HTML);
        aboutLayout.addComponent(label);
        aboutLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

        Panel panel = new Panel();
        panel.setContent(aboutLayout);

        return panel;
    }


    private Button getLogoButton() {
        ThemeResource logoResource = new ThemeResource("../a4am/img/A4-16-white.png");
        Button popupInvokerButton = new Button();
        popupInvokerButton.setStyleName(BaseTheme.BUTTON_LINK);
        popupInvokerButton.setIcon(logoResource);
        return popupInvokerButton;
    }

}
