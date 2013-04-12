/*
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations
 *  under the License.
 */

package com.sf.ddao.crud;

import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.orm.RSMapper;
import com.sf.ddao.orm.RSMapperFactoryRegistry;
import com.sf.ddao.orm.rsmapper.CollectionRSMapper;
import com.sf.ddao.orm.rsmapper.MapRSMapper;
import com.sf.ddao.orm.rsmapper.SingleRowRSMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.RowMapperFactory;

import org.apache.commons.chain.Context;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.sf.ddao.crud.param.CRUDParameterService.getCRUDDaoBean;

/**
 * Created by psyrtsov
 */
public class MapCRUDRSMapper implements RSMapper {
    RSMapper beanMapper;

    public Object handle(Context context, ResultSet rs) throws SQLException {
        if (beanMapper == null) {
            init(context);
        }
        return beanMapper.handle(context, rs);
    }

    private synchronized void init(Context ctx) {
        if (beanMapper != null) {
            return;
        }
        final MethodCallCtx callCtx = CtxHelper.get(ctx, MethodCallCtx.class);
        final Type returnType = callCtx.getMethod().getGenericReturnType();;
        Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
        RowMapperFactory keyMapperFactory = RSMapperFactoryRegistry.getScalarMapper(actualTypeArguments[0], 1, true);
        
        Class<?> beanClass = getCRUDDaoBean(ctx, -1);
        RowMapperFactory valueMapperFactory = RSMapperFactoryRegistry.getRowMapperFactory(beanClass);

        beanMapper = new MapRSMapper(keyMapperFactory, valueMapperFactory);
    }
}
