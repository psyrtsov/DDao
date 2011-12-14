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

import com.sf.ddao.orm.rsmapper.rowmapper.RowMapperFactory;
import org.apache.commons.chain.Context;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 8, 2007
 * Time: 4:45:07 PM
 */
public class ArrayRSMapper extends CollectionRSMapper {
    private final Class itemType;

    public ArrayRSMapper(RowMapperFactory rowMapper, Class itemType) {
        super(rowMapper);
        this.itemType = itemType;
    }

    @Override
    public Object handle(Context context, ResultSet rs) throws SQLException {
        Collection list = (Collection) super.handle(context, rs);
        Object[] array = (Object[]) Array.newInstance(itemType, list.size());
        //noinspection unchecked
        return list.toArray(array);
    }
}
