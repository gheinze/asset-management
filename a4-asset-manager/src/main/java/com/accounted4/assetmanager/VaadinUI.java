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
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;


/**
 * UI entry point.
 *
 * @author gheinze
 */
@SpringUI
@Theme("a4am")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class VaadinUI extends UI {

    public static final String LOCALE_KEY = "LOCALE";

    private final SpringViewProvider viewProvider;
    private final ApplicationContentArea viewContainer;
    private final ApplicationLayout applicationLayout;


    @Override
    protected void init(VaadinRequest request) {

        VaadinSession.getCurrent().setConverterFactory(new ConverterFactory());
        VaadinSession.getCurrent().setAttribute(LOCALE_KEY, Locale.CANADA);

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
