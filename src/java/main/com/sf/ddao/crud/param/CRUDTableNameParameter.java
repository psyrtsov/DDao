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

package com.sf.ddao.crud.param;

import com.sf.ddao.crud.TableName;
import com.sf.ddao.factory.param.ParameterException;
import com.sf.ddao.factory.param.ParameterHelper;
import org.apache.commons.chain.Context;

import static com.sf.ddao.crud.param.CRUDParameterService.getCRUDDaoBean;

/**
 * Created by psyrtsov
 */
public class CRUDTableNameParameter extends ParameterHelper {
    public static final String CRUD_TABLE_NAME = "crudTableName";
    private String tableName = null;

    public Object extractParam(Context context) throws ParameterException {
        if (tableName == null) {
            init(context);
        }
        return tableName;
    }

    private synchronized void init(Context context) {
        if (tableName != null) {
            return;
        }
        final Class<?> crudDaoBeanClass = getCRUDDaoBean(context);
        TableName tableName = crudDaoBeanClass.getAnnotation(TableName.class);
        if (tableName != null) {
            this.tableName = tableName.value();
        } else {
            this.tableName = crudDaoBeanClass.getSimpleName();
        }
    }
}
