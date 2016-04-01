package com.accounted4.assetmanager.util.spring.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject an SLF logger into a component via this Loggable annotation.
 * See: http://java.dzone.com/articles/inject-slf4j-logger-annotation
 * @author gheinze
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Loggable {
    //for slf4j
}
