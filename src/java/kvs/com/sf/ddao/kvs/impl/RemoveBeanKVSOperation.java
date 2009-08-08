package com.sf.ddao.kvs.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.StatementFactoryManager;
import com.sf.ddao.kvs.RemoveBean;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 9:00:31 PM
 */
public class RemoveBeanKVSOperation extends KVSOperationBase {
    private StatementFactory[] statementFactoryList;
    private RemoveBean annotation;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            String key = keyFactory.createText(args);
            keyValueStore.delete(key);
            for (StatementFactory statementFactory : statementFactoryList) {
                PreparedStatement preparedStatement = statementFactory.createStatement(connection, args);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        }
    }

    public void init(Method method) throws InitializerException {
        annotation = method.getAnnotation(RemoveBean.class);
        super.init(method, annotation.key());
        try {
            final String[] sqlList = annotation.sql();
            statementFactoryList = new StatementFactory[sqlList.length];
            for (int i = 0; i < sqlList.length; i++) {
                statementFactoryList[i] = StatementFactoryManager.createStatementFactory(method, sqlList[i]);
            }
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + method, e);
        }
    }
}
