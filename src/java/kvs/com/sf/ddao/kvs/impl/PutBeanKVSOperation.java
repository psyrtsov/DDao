package com.sf.ddao.kvs.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.StatementFactoryManager;
import com.sf.ddao.factory.param.ThreadLocalStatementParameter;
import com.sf.ddao.kvs.PutBean;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 6:57:37 PM
 */
public class PutBeanKVSOperation extends KVSOperationBase {
    public static final String ID_FIELD_NAME = "id";
    private StatementFactory statementFactory;
    private PutBean annotation;

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            PreparedStatement preparedStatement = statementFactory.createStatement(connection, args);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            String key = keyFactory.createText(args);
            keyValueStore.set(key, args[0]);
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        } finally {
            ThreadLocalStatementParameter.remove(ID_FIELD_NAME);
        }
    }

    public void init(Method method) throws InitializerException {
        annotation = method.getAnnotation(PutBean.class);
        super.init(method, annotation.key());
        try {
            statementFactory = StatementFactoryManager.createStatementFactory(method, annotation.sql());
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + method, e);
        }
    }
}
