package com.sf.ddao.astore.impl;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.astore.AsyncDB;
import com.sf.ddao.astore.AsyncDBBatchGet;
import com.sf.ddao.astore.DBBatchGet;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.param.DefaultParameter;
import com.sf.ddao.factory.param.Parameter;
import com.sf.ddao.handler.Intializible;
import com.sf.ddao.orm.RSMapperFactory;
import com.sf.ddao.orm.RSMapperFactoryRegistry;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class AsyncDBBatchGetOperation implements Command, Intializible {
    private final ALinker aLinker;
    private final Parameter cacheKeyParam;
    private final StatementFactory statementFactory;
    private Method method;
    private Class<? extends DBBatchGet> dbBatchGetClass;
    private RSMapperFactory mapRSMapperFactory;


    @Link
    public AsyncDBBatchGetOperation(DefaultParameter cacheKeyParam, StatementFactory statementFactory, ALinker aLinker) {
        this.cacheKeyParam = cacheKeyParam;
        this.statementFactory = statementFactory;
        this.aLinker = aLinker;
    }

    public boolean execute(Context context) throws Exception {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        Collection keyList = (Collection) cacheKeyParam.extractData(context);
        final AsyncDB asyncDB = AsyncStoreHandler.getAsyncDB(context);
        DBBatchGet dbBatchGet = aLinker.create(dbBatchGetClass);
        dbBatchGet.init(this, context);
        @SuppressWarnings({"unchecked"})
        Object res = asyncDB.batchGet(keyList, dbBatchGet);
        callCtx.setLastReturn(res);
        return CONTINUE_PROCESSING;
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        AsyncDBBatchGet asyncDBBatchGet = (AsyncDBBatchGet) annotation;
        final String sql = asyncDBBatchGet.sql();
        final String cacheKey = asyncDBBatchGet.cacheKey();
        dbBatchGetClass = asyncDBBatchGet.dbBatchGet();
        method = (Method) element;
        final Class<?> returnClass = method.getReturnType();
        if (!Map.class.isAssignableFrom(returnClass)) {
            throw new InitializerException("Method annotated with " + AsyncDBBatchGet.class + " has to have return type Map");
        }
        try {
            mapRSMapperFactory = RSMapperFactoryRegistry.create(method);
            cacheKeyParam.init(element, cacheKey);
            statementFactory.init(element, sql);
        } catch (Exception e) {
            throw new InitializerException("Failed to initialize sql operation for " + element, e);
        }
    }

    public Parameter getCacheKeyParam() {
        return cacheKeyParam;
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public Method getMethod() {
        return method;
    }

    public RSMapperFactory getMapORMapperFactory() {
        return mapRSMapperFactory;
    }
}
