package com.sf.ddao.chain;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pavel
 * Date: Aug 6, 2009
 * Time: 3:23:43 PM
 */
public class ChainInvocationContext {
    private Map<String, Object> data = new HashMap<String, Object>();
    private Object[] args;
    private Object lastReturn;
    private Method method;

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setLastReturn(Object returnValue) {
        this.lastReturn = returnValue;
    }

    public Object getLastReturn() {
        return lastReturn;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ChainInvocationContext{" +
                "args=" + (args == null ? null : Arrays.asList(args)) +
                ", data=" + data +
                ", lastReturn=" + lastReturn +
                ", method=" + method +
                '}';
    }
}
