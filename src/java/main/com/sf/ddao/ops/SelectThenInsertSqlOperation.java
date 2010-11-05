package com.sf.ddao.ops;

import com.sf.ddao.SelectThenInsert;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.param.ThreadLocalParameter;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

/**
 * User: Pavel Syrtsov
 * Date: Aug 2, 2008
 * Time: 9:12:57 PM
 */
public class SelectThenInsertSqlOperation extends UpdateSqlOperation {
    private SelectSqlOperation selectSqlOp;
    public static final String ID_FIELD_NAME = "id";

    @Link
    public SelectThenInsertSqlOperation(StatementFactory statementFactory, SelectSqlOperation selectSqlOp) {
        super(statementFactory);
        this.selectSqlOp = selectSqlOp;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        try {
            final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
            selectSqlOp.execute(context);
            Object res = callCtx.getLastReturn();
            ThreadLocalParameter.put(ID_FIELD_NAME, res);
            super.execute(context);
            callCtx.setLastReturn(res);
            return CONTINUE_PROCESSING;
        } finally {
            ThreadLocalParameter.remove(ID_FIELD_NAME);
        }
    }

    @Override
    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        SelectThenInsert selectThenInsert = element.getAnnotation(SelectThenInsert.class);
        String sql[] = selectThenInsert.value();
        if (sql.length != 2) {
            throw new InitializerException(SelectThenInsert.class.getSimpleName() + " annotation has to have 2 sql statments, but got:"
                    + Arrays.toString(sql) + ", for method " + element);
        }
        try {
            selectSqlOp.init(element, sql[0]);
            super.init(element, sql[1]);
        } catch (Exception e) {
            throw new InitializerException("Failed to setup sql operations " + Arrays.toString(sql) + " for method " + element, e);
        }
    }
}
