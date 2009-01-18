package com.sf.ddao.alinker;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by: Pavel
 * Date: Jan 11, 2009
 * Time: 9:58:47 PM
 */
public class CtxBuilder<T> {
    private final Class<T> clazz;
    private final List<Annotation> annotations = new ArrayList<Annotation>();

    public CtxBuilder(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static<C> CtxBuilder<C> create(Class<C> clazz) {
        return new CtxBuilder<C>(clazz);
    }

    public CtxBuilder<T> add(Class<? extends Annotation> annClass, Object ... values) {
        InvocationHandler ih = new AnnotationInvocationHandler(annClass, values);
        Annotation ann = (Annotation) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{annClass}, ih);
        annotations.add(ann);
        return this;
    }

    public Context<T> get() {
        Annotation[] aArray = annotations.toArray(new Annotation[annotations.size()]);
        return new Context<T>(clazz, aArray);
    }

    public static class AnnotationInvocationHandler implements InvocationHandler {
        private final Map<String,Object> valueMap = new HashMap<String,Object>();
        private final Class<? extends Annotation> annClass;

        public AnnotationInvocationHandler(Class<? extends Annotation> annClass, Object[] values) {
            this.annClass = annClass;
            if (values.length == 1) {
                this.valueMap.put("value", values[0]);
            } else {
                for (int i = 0; i < values.length; ) {
                    String name = values[i++].toString();
                    Object value = values[i++];
                    this.valueMap.put(name, value);
                }
            }
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                return method.invoke(valueMap, args);
            }
            if (declaringClass.equals(Annotation.class)) {
                return annClass;
            }
            String name = method.getName();
            return valueMap.get(name);
        }
    }
}
