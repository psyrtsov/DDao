package com.sf.ddao.astore.impl;

import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.astore.AsyncDB;
import com.sf.ddao.astore.AsyncDBPut;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.factory.param.DefaultParameter;
import com.sf.ddao.factory.param.Parameter;
import com.sf.ddao.factory.param.ParameterException;
import com.sf.ddao.handler.Intializible;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psyrtsov
 */
public class AsyncDBPutOperation implements Command, Intializible {
    private final StatementFactory statementFactory;
    private final Parameter cacheKeyParam;
    private final Parameter cacheValueParam;

    @Link
    public AsyncDBPutOperation(StatementFactory statementFactory, DefaultParameter cacheKeyParam, DefaultParameter cacheValueParam) {
        this.statementFactory = statementFactory;
        this.cacheKeyParam = cacheKeyParam;
        this.cacheValueParam = cacheValueParam;
    }

    public boolean execute(Context context) throws Exception {
        final String sql = statementFactory.createText(context);
        final List<Object> paramData = extractParamData(context);
        Command dbPut = new DBPutCommand(sql, paramData);
        Object key = cacheKeyParam.extractData(context);
        Object value = cacheValueParam.extractData(context);
        final AsyncDB asyncDB = AsyncStoreHandler.getAsyncDB(context);
        //noinspection unchecked
        asyncDB.put(key, value, dbPut);
        return CONTINUE_PROCESSING;
    }

    private List<Object> extractParamData(Context context) throws ParameterException {
        final List<Parameter> refParametersList = statementFactory.getRefParametersList();
        final List<Object> paramData = new ArrayList<Object>(refParametersList.size());
        for (Parameter parameter : refParametersList) {
            Object o = parameter.extractData(context);
            paramData.add(o);
        }
        return paramData;
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        AsyncDBPut asyncDBPut = (AsyncDBPut) annotation;
        final String sql = asyncDBPut.sql();
        final String cacheKey = asyncDBPut.cacheKey();
        final String cacheValue = asyncDBPut.cacheValue();
        try {
            cacheKeyParam.init(element, cacheKey);
            cacheValueParam.init(element, cacheValue);
            statementFactory.init(element, sql);
        } catch (StatementFactoryException e) {
            throw new InitializerException("Failed to setup sql operation " + sql + " for method " + element, e);
        }
    }
}
