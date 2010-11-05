package com.sf.ddao.astore.impl;

import com.sf.ddao.DaoException;
import com.sf.ddao.astore.DBBatchGet;
import com.sf.ddao.factory.param.JoinListParameter;
import com.sf.ddao.orm.ColumnMapper;
import com.sf.ddao.orm.ResultSetMapper;
import com.sf.ddao.orm.ResultSetMapperRegistry;
import org.apache.commons.chain.Context;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class DBBatchGetImpl implements DBBatchGet {
    public static final String KEY_LIST_CONTEXT_VALUE = "keyList";
    protected Context context;
    protected AsyncDBBatchGetOperation operation;

    public void init(AsyncDBBatchGetOperation operation, Context context) {
        this.operation = operation;
        this.context = context;
    }

    public Map batchGet(Collection keys) {
        try {
            //noinspection unchecked
            context.put(KEY_LIST_CONTEXT_VALUE, JoinListParameter.join(keys));
            PreparedStatement preparedStatement = operation.getStatementFactory().createStatement(context, Integer.MAX_VALUE);
            ResultSet resultSet = preparedStatement.executeQuery();
            Map map = loadData(resultSet);
            resultSet.close();
            preparedStatement.close();
            return map;
        } catch (Exception t) {
            throw new DaoException("Failed to execute sql operation for " + operation.getMethod(), t);
        }
    }

    protected Map loadData(ResultSet resultSet) throws Exception {
        final Type returnType = operation.getMethod().getGenericReturnType();
        Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
        Type keyType = actualTypeArguments[0]; // first arg in Map<K,V>
        Type valueType = actualTypeArguments[1]; // second arg in Map<K,V>
        ColumnMapper keyMapper = ResultSetMapperRegistry.getColumnMapper(keyType);
        ResultSetMapper valueMapper = ResultSetMapperRegistry.getResultMapper(valueType);
        Map map = new HashMap();
        if (resultSet != null) {
            while (resultSet.next()) {
                final Object key = keyMapper.get(resultSet, 1);
                valueMapper.addRecord(resultSet);
                final Object value = valueMapper.getResult();
                //noinspection unchecked
                map.put(key, value);
            }
        }
        return map;
    }
}
