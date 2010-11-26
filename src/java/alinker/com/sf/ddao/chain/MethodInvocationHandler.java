package com.sf.ddao.chain;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 2:29:05 PM
 */
class MethodInvocationHandler {
    private final Method method;
    private final Chain chain;
    private int contextParamIndex = -1;

    public MethodInvocationHandler(Method method, List<Command> commands) {
        this.method = method;
        this.chain = new ChainBase(commands);
        final Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            for (Annotation parameterAnnotation : parametersAnnotations[i]) {
                if (parameterAnnotation.annotationType().equals(UseContext.class)) {
                    contextParamIndex = i;
                    return;
                }
            }
        }
    }

    public Object invoke(Object[] args) throws Throwable {
        Context context = createContext(args);
        final MethodCallCtx callCtx = new MethodCallCtx(args, method);
        CtxHelper.put(context, MethodCallCtx.class, callCtx);
        chain.execute(context);
        if (Context.class.isAssignableFrom(method.getReturnType())) {
            return context;
        }
        return callCtx.getLastReturn();
    }

    private Context createContext(Object[] args) {
        Context context;
        if (contextParamIndex >= 0) {
            context = (Context) args[contextParamIndex];
            if (context == null) {
                throw new NullPointerException("Context parameter is null!");
            }
        } else {
            context = new ContextBase();
        }
        return context;
    }
}
