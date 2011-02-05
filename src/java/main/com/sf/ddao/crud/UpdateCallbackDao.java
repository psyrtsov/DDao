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

import com.sf.ddao.orm.UseRSMapper;

import static com.sf.ddao.crud.CRUDDao.CRUD_SELECT;
import static com.sf.ddao.factory.param.DefaultParameter.RETURN_ARG_IDX;
import static com.sf.ddao.crud.param.CRUDBeanPropsParameter.CRUD_BEAN_PROPS;
import static com.sf.ddao.crud.param.CRUDTableNameParameter.CRUD_TABLE_NAME;

/**
 * Created by tjokikorpela
 */
public interface UpdateCallbackDao<V> {

    public static final String CRUD_UPDATE =
            "update $" + CRUD_TABLE_NAME + ":" + RETURN_ARG_IDX + "$" +
                    " set #" + CRUD_BEAN_PROPS + ":" + RETURN_ARG_IDX + ",{0}=?#" +
                    " where id=#0#";

    @UseRSMapper(CRUDRSMapper.class)
    @SelectWithCallbackThenUpdate({CRUD_SELECT, CRUD_UPDATE})
    V update(Number key, UpdateCallback<V> callback);

}
