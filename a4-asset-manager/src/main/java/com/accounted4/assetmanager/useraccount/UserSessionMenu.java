package com.accounted4.assetmanager.useraccount;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.MenuBar;
import javax.annotation.PostConstruct;

/**
 *
 * @author gheinze
 */
@SpringComponent
@UIScope
public class UserSessionMenu extends MenuBar {


    private static final long serialVersionUID = 1L;

    private static final String LOGOUT = "Logout";


    @PostConstruct
    private void init() {
        enableLogout();
    }


    private final Command logoutCommand = (final MenuItem selectedItem) -> {
        Page.getCurrent().setLocation("/");
        getSession().getSession().invalidate();
    };


    public void enableLogout() {
        VaadinSession vaadinSession = getSession();
        if (null != vaadinSession) {
            UserSession userSession = (UserSession) vaadinSession.getAttribute(UserSession.USER_SESSION_KEY);
            if (null != userSession) {
                MenuBar.MenuItem userMenuItem = addItem(userSession.getDisplayName(), null, null);
                userMenuItem.addItem(LOGOUT, logoutCommand);
            }
        }
    }

}
