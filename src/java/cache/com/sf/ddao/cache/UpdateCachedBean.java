package com.sf.ddao.cache;

import com.sf.ddao.SqlAnnotation;
import com.sf.ddao.cache.impl.UpdateCachedBeanSqlOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 8:45:15 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@SqlAnnotation(UpdateCachedBeanSqlOperation.class)
public @interface UpdateCachedBean {
    String cache();

    String prefix() default "";
}
