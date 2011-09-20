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

import com.sf.ddao.Delete;
import com.sf.ddao.InsertAndGetGeneratedKey;
import com.sf.ddao.Select;
import com.sf.ddao.Update;
import com.sf.ddao.orm.UseRSMapper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.sf.ddao.crud.param.CRUDBeanPropsParameter.CRUD_BEAN_PROPS;
import static com.sf.ddao.crud.param.CRUDParameterService.USE_GENERICS;
import static com.sf.ddao.crud.param.CRUDTableNameParameter.CRUD_TABLE_NAME;
import static com.sf.ddao.factory.param.DefaultParameter.RETURN_ARG_IDX;

/**
 * Created by psyrtsov
 */
public interface CRUDDao<V> {
    public static final String ID_FIELD = "id";
    public static final Set<String> IGNORED_PROPS = new HashSet<String>() {{
        add("class");
        add(ID_FIELD);
    }};

    public static final String CRUD_INSERT =
            "insert into $" + CRUD_TABLE_NAME + ":0$" +
                    "($" + CRUD_BEAN_PROPS + ":0,{0}$)" +
                    " values(#" + CRUD_BEAN_PROPS + ":0,?#)";

    @InsertAndGetGeneratedKey(CRUD_INSERT)
    BigDecimal create(V bean);

    public static final String CRUD_SELECT =
            "select * from $" + CRUD_TABLE_NAME + ":" + RETURN_ARG_IDX + "$" +
                    " where id=#0# limit 1";

    @UseRSMapper(CRUDRSMapper.class)
    @Select(CRUD_SELECT)
    V read(Number key);

    public static final String CRUD_UPDATE =
            "update $" + CRUD_TABLE_NAME + ":0$" +
                    " set #" + CRUD_BEAN_PROPS + ":0,{0}=?#" +
                    " where id=#id#";

    @Update(CRUD_UPDATE)
    int update(V bean);

    @CheckIfBeanIsDirty
    @Update(CRUD_UPDATE)
    int updateIfDirty(V bean);

    public static final String CRUD_DELETE =
            "delete from $" + CRUD_TABLE_NAME + ":0$" +
                    " where id=#id#";

    @Delete("delete from $" + CRUD_TABLE_NAME + ":" + USE_GENERICS + "$ where id=#0#")
    int delete(Number id);
}
