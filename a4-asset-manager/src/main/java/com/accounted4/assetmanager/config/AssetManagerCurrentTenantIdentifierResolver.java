package com.accounted4.assetmanager.config;

import com.accounted4.assetmanager.ui.useraccount.UserSession;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 *
 * @author gheinze
 */
@Component
//@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AssetManagerCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT = "public";

    // Examples Autowire the HttpServletRequest and extract the tenant from there.
    // I'd like to pull the tenant out of the VaadinSession.  Will default for now.
    // TODO: logic to determine tenant
    @Override
    public String resolveCurrentTenantIdentifier() {
        UserSession userSession = getUserSession();
        return null == userSession ? DEFAULT_TENANT : userSession.getTenant();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }


    private UserSession getUserSession() {
        UI ui = UI.getCurrent();
        if (null != ui) {
            VaadinSession vaadinSession = VaadinSession.getCurrent();
            if (null != vaadinSession) {
                return (UserSession)vaadinSession.getAttribute(UserSession.USER_SESSION_KEY);
            }
        }
        return null;
    }

}
