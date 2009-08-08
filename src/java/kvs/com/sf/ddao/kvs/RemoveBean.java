package com.sf.ddao.kvs;

import com.sf.ddao.SqlAnnotation;
import com.sf.ddao.kvs.impl.RemoveBeanKVSOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 8:59:41 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@SqlAnnotation(RemoveBeanKVSOperation.class)
public @interface RemoveBean {
    String key();

    String[] sql() default {};
}
