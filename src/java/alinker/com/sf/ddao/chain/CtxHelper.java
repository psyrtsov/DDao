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

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import java.util.concurrent.Callable;

/**
 * Date: Oct 27, 2009
 * Time: 4:29:13 PM
 */
public class CtxHelper {
    public static <T> T get(Context ctx, Class<T> clazz) {
        //noinspection unchecked
        return (T) ctx.get(clazz.toString());
    }

    public static <T> T get(Context ctx, Class<T> clazz, Callable<T> callback) throws Exception {
        //noinspection unchecked
        T res = (T) ctx.get(clazz.toString());
        if (res == null) {
            res = callback.call();
            put(ctx, clazz, res);
        }
        return res;
    }

    public static <T> T put(Context ctx, Class<T> clazz, T value) {
        //noinspection unchecked
        return (T) ctx.put(clazz.toString(), value);
    }

    public static Context context(Object... argv) {
        Context res = new ContextBase();
        String key = null;
        for (Object o : argv) {
            if (key == null) {
                key = (String) o;
            } else {
                //noinspection unchecked
                res.put(key, o);
                key = null;
            }
        }
        return res;
    }

    public static <T> T remove(Context ctx, Class<T> clazz) {
        //noinspection unchecked
        return (T) ctx.remove(clazz.toString());
    }
}
