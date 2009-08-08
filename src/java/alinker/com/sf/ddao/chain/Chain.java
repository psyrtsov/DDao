package com.sf.ddao.chain;

import com.sf.ddao.alinker.factory.UseFactory;
import com.sf.ddao.handler.InvocationHandlerAnnotation;
import com.sf.ddao.handler.InvocationHandlerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annoation tells ALinker to use ChainHandler to handle invocations of interface methods
 * it enables to use Chain-of-responsibility pattern based on annotations
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@UseFactory(InvocationHandlerFactory.class)
@InvocationHandlerAnnotation(ChainInvocationHandler.class)
public @interface Chain {
}
