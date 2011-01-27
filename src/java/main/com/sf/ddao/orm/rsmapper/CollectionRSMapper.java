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

package com.sf.ddao.orm.rsmapper;

import com.sf.ddao.orm.RSMapper;
import com.sf.ddao.orm.RowMapper;
import org.apache.commons.chain.Context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 4:49:21 PM
 */
public class CollectionRSMapper implements RSMapper {
    private final Class<? extends Collection> listType;
    private final RowMapper rowMapper;


    public CollectionRSMapper(RowMapper rowMapper) {
        this(Collection.class, rowMapper);
    }

    public CollectionRSMapper(Class<? extends Collection> listType, RowMapper rowMapper) {
        this.listType = listType.isInterface() ? ArrayList.class : listType;
        this.rowMapper = rowMapper;
    }

    public Object handle(Context context, ResultSet rs) throws SQLException {
        Collection list;
        try {
            list = listType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
        while (rs.next()) {
            final Object o = rowMapper.map(rs);
            //noinspection unchecked
            list.add(o);
        }
        return list;
    }
}
