package com.accounted4.assetmanager.ui.useraccount;

import com.accounted4.assetmanager.service.UserAccountService;
import com.accounted4.assetmanager.entity.UserAccount;
import com.accounted4.assetmanager.UiRouter;
import com.accounted4.assetmanager.UiRouter.ViewName;
import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView(name = UiRouter.ViewName.LOGIN)
public class LoginSpringView extends Panel implements DefaultView {

    private final LoginForm loginForm = new LoginForm();
    @Inject private UserAccountService userAccountService;
    @Inject private UserSessionMenu userSessionMenu;


    @PostConstruct
    private void init() {
        loginForm.setEagerValidation(true);
        loginForm.setSavedHandler(this::saveClickedOnEntryForm);
        loginForm.setResetHandler(this::cancelClickedOnEntryForm);
        loginForm.setModalWindowTitle("Login");
    }



    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //DefaultView.super.enter(event);
        loginForm.setEntity(new LoginFormBean());
        loginForm.openInModalPopup();
    }



    private void saveClickedOnEntryForm(final LoginFormBean loginFormBean) {

        try {
            UserAccount authenticatedUser = userAccountService.authenticate(loginFormBean.getUserAccount(), loginFormBean.getPassword());

            UserSession userSession = new UserSession();
            userSession.setDisplayName(authenticatedUser.getDisplayName());
            userSession.setTenant(authenticatedUser.getTenant());
            userSession.setUserAccountName(authenticatedUser.getName());

            VaadinSession vaadinSession = VaadinSession.getCurrent();
            if (null != vaadinSession.getAttribute(UserSession.USER_SESSION_KEY)) {
                new Notification("Login aborted: user is already active in another tab", "", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
            } else {
                VaadinSession.getCurrent().setAttribute(UserSession.USER_SESSION_KEY, userSession);
            }
            userSessionMenu.enableLogout();

            closeWindow();

            // Need to go to some view other than the LoginView or TODO: welcome landing page?
            getUI().getNavigator().navigateTo(ViewName.PAYMENT_CALCULATOR);

        } catch (EmptyResultDataAccessException enfe) {
            new Notification("Authentication failure", "", Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
        }

    }

    private void cancelClickedOnEntryForm(final LoginFormBean userAccount) {
        //this.navigator.setErrorView(myView);
        closeWindow();
        // Navigate somewhere to force a login window, otherwise we are at a page with no usersession
        getUI().getNavigator().navigateTo(ViewName.PAYMENT_CALCULATOR);
    }

    private void closeWindow() {

        // Order of window removal is important: first get rid of login window, then wrapper window
        Collection<Window> childWindows = getUI().getWindows();
        childWindows.stream()
                .filter((w) -> (w.getContent() instanceof LoginForm))
                .forEach((w) -> { getUI().removeWindow(w); }
        );

        childWindows = getUI().getWindows();
        childWindows.stream()
                .forEach((w) -> { getUI().removeWindow(w); }
        );
    }


}
