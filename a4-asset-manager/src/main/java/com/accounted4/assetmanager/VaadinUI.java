package com.accounted4.assetmanager;

import com.accounted4.assetmanager.useraccount.LoginSpringView;
import com.accounted4.assetmanager.useraccount.UserSession;
import com.accounted4.assetmanager.util.vaadin.converter.ConverterFactory;
import com.vaadin.annotations.PreserveOnRefresh;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
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
@Title("Accounted4 Asset Management")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class VaadinUI extends UI implements ViewChangeListener {

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
        navigator.addViewChangeListener(this);
        navigator.navigateTo(UiRouter.ViewName.PAYMENT_CALCULATOR);  // initial landing page

        // TODO: Add an error view
        // navigator.setErrorView(myView);

        applicationLayout.setSizeFull();
        setContent(applicationLayout);
        setSizeUndefined();

    }


    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {

        View requestedView = event.getNewView();

        if (!(requestedView instanceof LoginSpringView) && null == VaadinSession.getCurrent().getAttribute(UserSession.USER_SESSION_KEY)) {
            getNavigator().navigateTo(UiRouter.ViewName.LOGIN);
            return false;
        }

        return true;
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
    }


}
