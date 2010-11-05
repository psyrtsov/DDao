package com.sf.ddao.astore;

import com.sf.ddao.astore.impl.AsyncDBBatchGetOperation;
import com.sf.ddao.astore.impl.DBBatchGetImpl;
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
@CommandAnnotation(AsyncDBBatchGetOperation.class)
public @interface AsyncDBBatchGet {
    String sql();

    String cacheKey() default "0";

    Class<? extends DBBatchGet> dbBatchGet() default DBBatchGetImpl.class;
}
