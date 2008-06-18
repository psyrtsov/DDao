/**
 * Copyright 2008 Pavel Syrtsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */

package com.syrtsov.ddao;

/**
 * SelectCallback is callback interface that can be used together with Select
 * query annotation. When method associated with Select query has return type void
 * it assumed to be using callback. Handler will find first method argument implemennting
 * this interface and will invoke process record for each record that query returns.
 * <p/>
 * Created by: Pavel Syrtsov
 * Date: Apr 6, 2007
 * Time: 7:17:36 PM
 */
public interface SelectCallback<T> {
    /**
     * this method will be invoked for each record that query returns.
     *
     * @param record - object that query record is mapped to.
     * @return true if more records should be processed,
     *         if return value is false record processing will be stopped
     */
    boolean processRecord(T record);
}
