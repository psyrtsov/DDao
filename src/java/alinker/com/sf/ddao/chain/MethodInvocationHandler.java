package com.sf.ddao.chain;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 2:29:05 PM
 */
class MethodInvocationHandler {
    private final Method method;
    private final List<ChainMemberInvocationHandler> invocationHandlers;
    private int chainContextParamIndex = -1;
    private List<ChainInvocationPostProcessor> postProcessors = new ArrayList<ChainInvocationPostProcessor>();

    public MethodInvocationHandler(Method method, List<ChainMemberInvocationHandler> invocationHandlers) {
        this.method = method;
        this.invocationHandlers = invocationHandlers;
        final Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (ChainInvocationContext.class.isAssignableFrom(parameterTypes[i])) {
                chainContextParamIndex = i;
                break;
            }
        }
        for (ChainMemberInvocationHandler invocationHandler : invocationHandlers) {
            if (invocationHandler instanceof ChainInvocationPostProcessor) {
                ChainInvocationPostProcessor postProcessor = (ChainInvocationPostProcessor) invocationHandler;
                postProcessors.add(postProcessor);
            }
        }
    }

    public Object invoke(Object[] args) throws Throwable {
        ChainInvocationContext context = createContext(args);
        context.setMethod(method);
        for (Iterator<ChainMemberInvocationHandler> it = invocationHandlers.iterator(); it.hasNext();) {
            ChainMemberInvocationHandler invocationHandler = it.next();
            Object res = invocationHandler.invoke(context, it.hasNext());
            context.setLastReturn(res);
        }
        for (ChainInvocationPostProcessor postProcessor : postProcessors) {
            postProcessor.chainPostProcess(context);
        }
        if (ChainInvocationContext.class.isAssignableFrom(method.getReturnType())) {
            return context;
        }
        return context.getLastReturn();
    }

    private ChainInvocationContext createContext(Object[] args) {
        ChainInvocationContext res;
        if (chainContextParamIndex >= 0) {
            res = (ChainInvocationContext) args[chainContextParamIndex];
            if (res == null) {
                throw new NullPointerException("ChainInvocationContext parameter is null!");
            }
        } else {
            res = new ChainInvocationContext();
        }
        res.setArgs(args);
        return res;
    }
}
