package com.sf.ddao.kvs.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.kvs.UpdateBean;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 8:46:34 PM
 */
public class UpdateBeanKVSOperation extends KVSOperationBase {
    private UpdateBean annotation;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            String key = annotation.prefix() + args[0].toString();
            keyValueStore.set(key, args[1]);
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(Method method) throws InitializerException {
        annotation = method.getAnnotation(UpdateBean.class);
        super.init(method);
    }
}
