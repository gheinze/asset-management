package com.accounted4.assetmanager;

import com.accounted4.assetmanager.util.vaadin.converter.ConverterFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import java.util.Locale;


/**
 * UI entry point.
 *
 * @author gheinze
 */
@SpringUI
@Theme("a4am")
public class VaadinUI extends UI {

    @Autowired private SpringViewProvider viewProvider;
    @Autowired private ApplicationContentArea viewContainer;
    @Autowired private ApplicationLayout applicationLayout;


    public static final String LOCALE_KEY = "LOCALE";

    public VaadinUI() {
        VaadinSession.getCurrent().setConverterFactory(new ConverterFactory());
        VaadinSession.getCurrent().setAttribute(LOCALE_KEY, Locale.CANADA);
    }

    @Override
    protected void init(VaadinRequest request) {

        Navigator navigator = new Navigator(UI.getCurrent(), viewContainer);
        navigator.addProvider(viewProvider);

        navigator.navigateTo(UiRouter.ViewName.PAYMENT_CALCULATOR);  // default view

        // TODO: Add an error view
        // navigator.setErrorView(myView);

        applicationLayout.setSizeFull();
        setContent(applicationLayout);
        setSizeUndefined();

    }

}
