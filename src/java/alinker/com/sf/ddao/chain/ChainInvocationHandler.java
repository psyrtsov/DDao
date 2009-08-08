package com.sf.ddao.chain;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.handler.Intializible;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This invocation handler allows to enterpret few annotations attached to method as chain of comamnds to be executed
 * when method is invoked
 * <p/>
 */
public class ChainInvocationHandler implements InvocationHandler, Intializible {
    Map<Method, MethodInvocationHandler> map = new HashMap<Method, MethodInvocationHandler>();

    @Inject
    public ALinker aLinker;

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        final MethodInvocationHandler methodHandler = map.get(method);
        return methodHandler.invoke(args);
    }

    @Override
    public void init(AnnotatedElement iFace, Annotation annotation) throws InitializerException {
        List<ChainMemberInvocationHandler> classLevelList = new ArrayList<ChainMemberInvocationHandler>();
        addChainMemebers(iFace, classLevelList);
        final Method[] methods = ((Class<?>) iFace).getMethods();
        for (Method method : methods) {
            List<ChainMemberInvocationHandler> list = new ArrayList<ChainMemberInvocationHandler>(classLevelList.size() + method.getAnnotations().length);
            list.addAll(classLevelList);
            addChainMemebers(method, list);
            map.put(method, new MethodInvocationHandler(method, list));
        }
    }

    private void addChainMemebers(AnnotatedElement annotatedElement, List<ChainMemberInvocationHandler> list) {
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            final ChainMember memberAnnotation = annotation.annotationType().getAnnotation(ChainMember.class);
            final ChainMemberInvocationHandler memberInvocationHandler = aLinker.create(memberAnnotation.value());
            if (memberInvocationHandler instanceof Intializible) {
                Intializible intializible = (Intializible) memberInvocationHandler;
                intializible.init(annotatedElement, annotation);
            }
            list.add(memberInvocationHandler);
        }
    }

}
