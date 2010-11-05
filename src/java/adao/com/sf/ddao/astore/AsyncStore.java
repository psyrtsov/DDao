package com.sf.ddao.astore;

import com.sf.ddao.alinker.factory.UseFactory;
import com.sf.ddao.astore.impl.AsyncStoreHandler;
import com.sf.ddao.chain.ChainHandlerFactory;
import com.sf.ddao.chain.CommandAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by psyrtsov
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@UseFactory(ChainHandlerFactory.class)
@CommandAnnotation(AsyncStoreHandler.class)
public @interface AsyncStore {
    Class<? extends AsyncDB> value();
}
