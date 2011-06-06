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

package com.sf.ddao.orm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tojoko
 */
public class ColumnMapperRegistry {
    private static ColumnMapperRegistry instance = new ColumnMapperRegistry();

    private final Map<Class, ColumnMapper> columnMappers;

    private ColumnMapperRegistry() {
        columnMappers = new ConcurrentHashMap<Class, ColumnMapper>();
    }

    public static ColumnMapper lookup(java.lang.Class clazz) {
        return instance.columnMappers.get(clazz);
    }

    public static void register(java.lang.Class clazz, ColumnMapper columnMapper) {
        instance.columnMappers.put(clazz, columnMapper);
    }
}
