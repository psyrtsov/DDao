package com.syrtsov.ddao.ops;

import com.syrtsov.ddao.factory.StatementFactory;
import com.syrtsov.ddao.factory.StatementFactoryManager;
import com.syrtsov.ddao.factory.param.ThreadLocalStatementParameter;
import com.syrtsov.ddao.DaoException;
import com.syrtsov.ddao.SqlOperation;
import com.syrtsov.ddao.SelectThenInsert;
import com.syrtsov.ddao.alinker.initializer.InitializerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * User: Pavel Syrtsov
 * Date: Aug 2, 2008
 * Time: 9:12:57 PM
 * psdo: provide comments for class ${CLASSNAME}
 */
public class SelectThenInsertSqlOperation implements SqlOperation {
    private SelectSqlOperation selectSqlOp;
    private StatementFactory insertStatementFactory;
    public static final String ID_FIELD_NAME = "id";

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            Object res = selectSqlOp.invoke(connection, method, args);
            ThreadLocalStatementParameter.put(ID_FIELD_NAME, res);
            PreparedStatement preparedStatement = insertStatementFactory.createStatement(connection, args);
            preparedStatement = insertStatementFactory.createStatement(connection, args);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return res;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + method, t);
        } finally {
            ThreadLocalStatementParameter.remove(ID_FIELD_NAME);            
        }
    }

    public void init(Method method) throws InitializerException {
        SelectThenInsert annotation = method.getAnnotation(SelectThenInsert.class);
        String sql[] = annotation.value();
        if (sql.length != 2) {
            throw new InitializerException(SelectThenInsert.class.getSimpleName() + " annotation has to have 2 sql statments, but got:"
                    + Arrays.toString(sql) + ", for method " + method);
        }
        try {
            selectSqlOp = new SelectSqlOperation();
            selectSqlOp.init(method, sql[0]);
            insertStatementFactory = StatementFactoryManager.createStatementFactory(method, sql[1]);
        } catch (Exception e) {
            throw new InitializerException("Failed to setup sql operation " + Arrays.toString(sql) + " for method " + method, e);
        }
    }
}
