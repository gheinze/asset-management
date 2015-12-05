package com.accounted4.assetmanager.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * A hack to record a reference to the Spring application context.
 * @author gheinze
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        ApplicationContextUtil.setApplicationContext(ctx);
    }
}
