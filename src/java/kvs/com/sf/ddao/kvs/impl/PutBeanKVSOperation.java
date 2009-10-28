package com.sf.ddao.kvs.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.param.ThreadLocalParameter;
import com.sf.ddao.kvs.PutBean;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
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
    private AnnotatedElement element;

    @Inject
    public PutBeanKVSOperation(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            Connection connection = ConnectionHandlerHelper.getConnection(context);
            final Object[] args = callCtx.getArgs();
            PreparedStatement preparedStatement = statementFactory.createStatement(context);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            String key = keyFactory.createText(context);
            keyValueStore.set(key, args[0]);
            return CONTINUE_PROCESSING;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + element, t);
        } finally {
            ThreadLocalParameter.remove(ID_FIELD_NAME);
        }
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        PutBean putBean = (PutBean) annotation;
        this.element = element;
        super.init(element, putBean.key());
        try {
            statementFactory.init(element, putBean.sql());
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to initialize sql operation for " + element, e);
        }
    }

}
