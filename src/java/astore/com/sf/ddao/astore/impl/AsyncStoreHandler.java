package com.sf.ddao.astore.impl;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.astore.AsyncDB;
import com.sf.ddao.astore.AsyncStore;
import com.sf.ddao.handler.Intializible;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Created by psyrtsov
 */
public class AsyncStoreHandler implements Filter, Intializible {
    public static final String ASYNC_DAO_HANDLER_KEY = AsyncStoreHandler.class.toString();

    @Link
    public ALinker aLinker;
    private AsyncDB asyncDB;

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        AsyncStore asyncStore = (AsyncStore) annotation;
        asyncDB = aLinker.create(asyncStore.value());
    }

    public boolean execute(Context context) throws Exception {
        //noinspection unchecked
        context.put(ASYNC_DAO_HANDLER_KEY, this);
        return CONTINUE_PROCESSING;
    }

    public boolean postprocess(Context context, Exception e) {
        context.remove(ASYNC_DAO_HANDLER_KEY);
        return CONTINUE_PROCESSING;
    }

    public static AsyncDB getAsyncDB(Context context) {
        final AsyncStoreHandler asyncStoreHandler = (AsyncStoreHandler) context.get(ASYNC_DAO_HANDLER_KEY);
        return asyncStoreHandler.asyncDB;
    }

}
