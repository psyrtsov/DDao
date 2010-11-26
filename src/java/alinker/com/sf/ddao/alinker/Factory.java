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

package com.sf.ddao.alinker;

/**
 * Note : factory scope defined by method createFactory in DefaultFactr
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 8:03:33 PM
 */
public interface Factory<T> {
    T create(ALinker aLinker, Context<T> ctx) throws FactoryException;
}
