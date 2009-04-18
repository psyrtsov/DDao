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

package com.sf.ddao.orm;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 14, 2007
 * Time: 4:20:21 PM
 */
public class ResultSetMapperException extends Exception {
    public ResultSetMapperException() {
    }

    public ResultSetMapperException(String message) {
        super(message);
    }

    public ResultSetMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResultSetMapperException(Throwable cause) {
        super(cause);
    }
}
