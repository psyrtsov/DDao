package com.sf.ddao.kvs.impl;

import com.sf.ddao.SqlOperation;
import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.kvs.KeyValueStore;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by pavel
 * Date: Jul 26, 2009
 * Time: 12:01:37 AM
 */
public abstract class KVSOperationBase implements SqlOperation {
    @Inject
    public ALinker aLinker;
    protected KeyValueStore<String, Object> keyValueStore;

    public void init(Method method) {
        final Annotation[] methodAnnotations = method.getAnnotations();
        final Annotation[] classAnnotations = method.getClass().getAnnotations();
        Annotation[] annotations = new Annotation[methodAnnotations.length + classAnnotations.length];
        System.arraycopy(methodAnnotations, 0, annotations, 0, methodAnnotations.length);
        System.arraycopy(classAnnotations, 0, annotations, methodAnnotations.length, classAnnotations.length);
        final Context<KeyValueStore> context = new Context<KeyValueStore>(KeyValueStore.class, annotations, method, 0);
        //noinspection unchecked
        keyValueStore = aLinker.create(context);
    }
}
