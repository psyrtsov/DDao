package com.sf.ddao.kvs.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.chain.ChainInvocationContext;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.kvs.RemoveBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by pavel
 * Date: Jul 22, 2009
 * Time: 9:00:31 PM
 */
public class RemoveBeanKVSOperation extends KVSOperationBase {
    @Inject
    public ALinker aLinker;
    private StatementFactory[] statementFactoryList;

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        RemoveBean removeBean = (RemoveBean) annotation;
        super.init(element, removeBean.key());
        try {
            final String[] sqlList = removeBean.sql();
            statementFactoryList = new StatementFactory[sqlList.length];
            for (int i = 0; i < sqlList.length; i++) {
                statementFactoryList[i] = aLinker.create(StatementFactory.class);
                statementFactoryList[i].init(element, sqlList[i]);
            }
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + element, e);
        }
    }

    public Object invoke(ChainInvocationContext context, boolean hasNext) throws Throwable {
        try {
            String key = keyFactory.createText(context.getArgs());
            keyValueStore.delete(key);
            for (StatementFactory statementFactory : statementFactoryList) {
                Connection connection = ConnectionHandlerHelper.getConnection(context);
                PreparedStatement preparedStatement = statementFactory.createStatement(connection, context.getArgs());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            return null;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + context, t);
        }
    }
}
