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

package com.sf.ddao.shards;

import com.sf.ddao.Delete;
import com.sf.ddao.InsertAndGetGeneratedKey;
import com.sf.ddao.Select;
import com.sf.ddao.Update;
import com.sf.ddao.crud.CRUDRSMapper;
import com.sf.ddao.orm.UseRSMapper;

import java.math.BigDecimal;

import static com.sf.ddao.crud.CRUDDao.*;
import static com.sf.ddao.crud.param.CRUDParameterService.USE_GENERICS;
import static com.sf.ddao.crud.param.CRUDTableNameParameter.CRUD_TABLE_NAME;

/**
 * Created by psyrtsov
 */
public interface ShardedCRUDDao<V, K> {
    @InsertAndGetGeneratedKey(CRUD_INSERT)
    BigDecimal create(V bean, @ShardKey K key);

    @InsertAndGetGeneratedKey(CRUD_INSERT)
    BigDecimal create(@ShardKey(ID_FIELD) V bean);

    @UseRSMapper(CRUDRSMapper.class)
    @Select(CRUD_SELECT)
    V read(Number id, @ShardKey K key);

    @UseRSMapper(CRUDRSMapper.class)
    @Select(CRUD_SELECT)
    V read(@ShardKey Number id);

    @Update(CRUD_UPDATE)
    int update(V bean, @ShardKey K key);

    @Update(CRUD_UPDATE)
    int update(@ShardKey(ID_FIELD) V bean);

    @Delete("delete from $" + CRUD_TABLE_NAME + ":" + USE_GENERICS + "$ where id=#0#")
    int delete(Number id, @ShardKey K key);

    @Delete("delete from $" + CRUD_TABLE_NAME + ":" + USE_GENERICS + "$ where id=#0#")
    int delete(@ShardKey Number id);
}
