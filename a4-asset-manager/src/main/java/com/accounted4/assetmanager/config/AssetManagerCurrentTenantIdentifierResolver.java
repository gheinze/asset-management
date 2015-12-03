package com.accounted4.assetmanager.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 *
 * @author gheinze
 */
@Component
//@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AssetManagerCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT = "tenant_0";

    // Examples Autowire the HttpServletRequest and extract the tenant from there.
    // I'd like to pull the tenant out of the VaadinSession.  Will default for now.
    // TODO: logic to determine tenant
    @Override
    public String resolveCurrentTenantIdentifier() {
        return DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
