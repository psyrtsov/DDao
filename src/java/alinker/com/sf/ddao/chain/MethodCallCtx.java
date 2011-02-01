/*
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations
 *  under the License.
 */

package com.sf.ddao.chain;

import java.lang.reflect.Method;

/**
 * Date: Oct 23, 2009
 * Time: 4:14:06 PM
 */
@SuppressWarnings({"unchecked"})
public class MethodCallCtx {
    private Method method;
    private Class<?> subjClass;
    private Object[] args;
    private Object lastReturn;

    public MethodCallCtx(Object[] args, Method method, Class<?> subjClass) {
        this.args = args;
        this.method = method;
        this.subjClass = subjClass;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getLastReturn() {
        return lastReturn;
    }

    public void setLastReturn(Object lastReturn) {
        this.lastReturn = lastReturn;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getSubjClass() {
        return subjClass;
    }

    public void setSubjClass(Class<?> subjClass) {
        this.subjClass = subjClass;
    }
}
