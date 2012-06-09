package com.sf.ddao.chain;

import com.google.inject.AbstractModule;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by psyrtsov
 */
public class ChainModule extends AbstractModule {
    private final Class<?>[] chainClassList;

    public ChainModule(Class<?>... chainClassList) {
        this.chainClassList = chainClassList;
    }

    protected void configure() {
        final Set<Class<?>> classes;
        try {
            classes = PackageScanner.getClasses(chainClassList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        classes.addAll(Arrays.asList(chainClassList));
        for (Class<?> aClass : classes) {
            if (!aClass.isInterface()) {
                continue;
            }
            final Annotation[] annotations = aClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(CommandAnnotation.class)) {
                    bindChain(aClass);
                    break;
                }
            }
        }
    }

    protected void bindChain(Class<?> aClass) {
        //noinspection unchecked
        bind(aClass).toProvider(new ChainHandlerProvider(getProvider(ChainInvocationHandler.class), aClass));
    }
}
