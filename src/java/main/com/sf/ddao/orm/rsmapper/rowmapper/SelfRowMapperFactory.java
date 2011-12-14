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

package com.sf.ddao.orm.rsmapper.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class SelfRowMapperFactory implements RowMapperFactory, RowMapper {
    private final Class<? extends RowMapper> resultClass;

    public SelfRowMapperFactory(Class<? extends RowMapper> resultClass) {
        this.resultClass = resultClass;
    }

    public Object map(ResultSet rs) throws SQLException {
        RowMapper result;
        try {
            result = resultClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result.map(rs);
    }

    public RowMapper get() {
        return this;
    }
}
