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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class MapRSMapper implements RSMapper {
    private final RowMapper keyMapper;
    private final RowMapper valueMapper;

    public MapRSMapper(RowMapper keyMapper, RowMapper valueMapper) {
        this.keyMapper = keyMapper;
        this.valueMapper = valueMapper;
    }

    public Object handle(ResultSet rs) throws SQLException {
        Map res = new HashMap();
        while (rs.next()) {
            Object key = keyMapper.map(rs);
            Object value = valueMapper.map(rs);
            //noinspection unchecked
            res.put(key, value);
        }
        return res;
    }
}
