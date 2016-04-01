package com.accounted4.assetmanager.util.spring.log;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * PostBeanProcessor to find member variables annotated with the @Loggable
 * and set the member to an slf logger.
 *
 * See: http://java.dzone.com/articles/inject-slf4j-logger-annotation
 * @author gheinze
 */
@Component
public class LoggableInjector implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), (Field field) -> {

            // make the field accessible if defined private
            ReflectionUtils.makeAccessible(field);

            if (field.getAnnotation(Loggable.class) != null) {
                Logger log = LoggerFactory.getLogger(bean.getClass());
                field.set(bean, log);
            }

        });

        return bean;

    }

}
