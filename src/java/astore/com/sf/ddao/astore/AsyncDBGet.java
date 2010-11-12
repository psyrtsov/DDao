package com.sf.ddao.astore;

import com.sf.ddao.astore.impl.AsyncDBGetOperation;
import com.sf.ddao.chain.CommandAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by psyrtsov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CommandAnnotation(AsyncDBGetOperation.class)
public @interface AsyncDBGet {
    String sql();

    String cacheKey();
}
