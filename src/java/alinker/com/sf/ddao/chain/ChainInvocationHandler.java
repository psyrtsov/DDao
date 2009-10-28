package com.sf.ddao.chain;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.handler.Intializible;
import org.apache.commons.chain.Command;

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
public class ChainInvocationHandler implements InvocationHandler {
    Map<Method, MethodInvocationHandler> map = new HashMap<Method, MethodInvocationHandler>();

    @Inject
    public ALinker aLinker;

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        final MethodInvocationHandler methodHandler = map.get(method);
        return methodHandler.invoke(args);
    }

    public void init(AnnotatedElement iFace) throws InitializerException {
        List<Command> classLevelList = new ArrayList<Command>();
        addChainMemebers(iFace, classLevelList);
        final Method[] methods = ((Class<?>) iFace).getMethods();
        for (Method method : methods) {
            List<Command> list = new ArrayList<Command>(classLevelList.size() + method.getAnnotations().length);
            list.addAll(classLevelList);
            addChainMemebers(method, list);
            map.put(method, new MethodInvocationHandler(method, list));
        }
    }

    private void addChainMemebers(AnnotatedElement annotatedElement, List<Command> list) {
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            final CommandAnnotation memberAnnotation = annotation.annotationType().getAnnotation(CommandAnnotation.class);
            if (memberAnnotation == null) {
                continue;
            }
            final Command chainCommand = aLinker.create(memberAnnotation.value());
            if (chainCommand instanceof Intializible) {
                Intializible intializible = (Intializible) chainCommand;
                intializible.init(annotatedElement, annotation);
            }
            list.add(chainCommand);
        }
    }

}
