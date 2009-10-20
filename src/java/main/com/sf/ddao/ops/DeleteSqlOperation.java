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

package com.sf.ddao.ops;

import com.sf.ddao.Delete;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.factory.StatementFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Created by: Pavel Syrtsov
 * Date: Apr 24, 2007
 * Time: 1:44:52 AM
 */
public class DeleteSqlOperation extends UpdateSqlOperation {
    @Inject
    public DeleteSqlOperation(StatementFactory statementFactory) {
        super(statementFactory);
    }

    @Override
    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        Delete deleteAnnotation = (Delete) annotation;
        init(element, deleteAnnotation.value());
    }
}
