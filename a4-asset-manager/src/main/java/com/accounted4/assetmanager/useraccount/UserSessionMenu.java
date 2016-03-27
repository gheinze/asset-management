package com.accounted4.assetmanager.useraccount;

import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.MenuBar;

/**
 *
 * @author gheinze
 */
@SpringComponent
@UIScope
public class UserSessionMenu extends MenuBar {

    private static final String LOGOUT = "Logout";


    private final Command logoutCommand = (final MenuItem selectedItem) -> {
        Page.getCurrent().setLocation("/");
        getSession().getSession().invalidate();
    };


    public void enableLogout() {
        UserSession userSession = (UserSession)getSession().getAttribute(UserSession.USER_SESSION_KEY);
        MenuBar.MenuItem userMenuItem = addItem(userSession.getDisplayName(), null, null);
        userMenuItem.addItem(LOGOUT, logoutCommand);
    }

}
