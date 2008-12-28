package com.sf.ddao.ops;

import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryManager;
import com.sf.ddao.factory.param.ThreadLocalStatementParameter;
import com.sf.ddao.DaoException;
import com.sf.ddao.SqlOperation;
import com.sf.ddao.SelectThenInsert;
import com.sf.ddao.alinker.initializer.InitializerException;

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
public class SelectThenInsertSqlOperation extends UpdateSqlOperation {
    private SelectSqlOperation selectSqlOp;
    public static final String ID_FIELD_NAME = "id";

    public Object invoke(Connection connection, Method method, Object[] args) {
        try {
            Object res = selectSqlOp.invoke(connection, method, args);
            ThreadLocalStatementParameter.put(ID_FIELD_NAME, res);
            super.invoke(connection,method,args);
            return res;
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
            super.init(method, sql[1]);
        } catch (Exception e) {
            throw new InitializerException("Failed to setup sql operations " + Arrays.toString(sql) + " for method " + method, e);
        }
    }
}
