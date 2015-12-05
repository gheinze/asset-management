package com.accounted4.assetmanager.config;

import org.springframework.context.ApplicationContext;

/**
 * Provide a means to reference the Spring application context. Generally shouldn't be used. Added as
 * a work around to avoid DataSource serialization issues in AssetManagerMultiTenantConnectionProvider.
 * TODO: revisit injected DataSource member of AssetManagerMultiTenantConnectionProvider.
 * @author gheinze
 */
public class ApplicationContextUtil {

    private static ApplicationContext ctx = null;

    private ApplicationContextUtil() {
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        ApplicationContextUtil.ctx = ctx;
    }

}
