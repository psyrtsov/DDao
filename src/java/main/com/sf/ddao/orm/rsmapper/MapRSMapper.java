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
import com.sf.ddao.orm.rsmapper.rowmapper.RowMapper;
import com.sf.ddao.orm.rsmapper.rowmapper.RowMapperFactory;
import org.apache.commons.chain.Context;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class MapRSMapper implements RSMapper {
    private final RowMapperFactory keyMapperFactory;
    private final RowMapperFactory valueMapperFactory;

    public MapRSMapper(RowMapperFactory keyMapperFactory, RowMapperFactory valueMapperFactory) {
        this.keyMapperFactory = keyMapperFactory;
        this.valueMapperFactory = valueMapperFactory;
    }

    public Object handle(Context context, ResultSet rs) throws SQLException {
        Map res = new HashMap();
        final RowMapper keyMapper = keyMapperFactory.get();
        final RowMapper valueMapper = valueMapperFactory.get();
        while (rs.next()) {
            Object key = keyMapper.map(rs);
            Object value = valueMapper.map(rs);
            //noinspection unchecked
            res.put(key, value);
        }
        return res;
    }
}
