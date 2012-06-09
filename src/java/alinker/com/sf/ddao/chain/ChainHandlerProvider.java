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

import com.google.inject.Provider;

import java.lang.reflect.Proxy;

/**
 * factory is registered one instance per class/annotaion combination
 * <p/>
 * Date: Oct 23, 2009
 * Time: 2:37:52 PM
 */
public class ChainHandlerProvider<T> implements Provider<T> {
    private final Provider<ChainInvocationHandler> provider;
    private final Class<T> iFace;

    public ChainHandlerProvider(Provider<ChainInvocationHandler> provider, Class<T> iFace) {
        this.provider = provider;
        this.iFace = iFace;
    }

    public T get() {
        final Class[] iFaceList = {iFace};
        ChainInvocationHandler cih = provider.get();
        cih.init(iFace);
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(iFace.getClassLoader(), iFaceList, cih);
    }
}
