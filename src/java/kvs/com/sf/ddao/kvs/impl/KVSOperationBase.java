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

package com.sf.ddao.kvs.impl;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.StatementFactoryException;
import com.sf.ddao.handler.Intializible;
import com.sf.ddao.kvs.KVSException;
import com.sf.ddao.kvs.KeyValueStore;
import org.apache.commons.chain.Command;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Created by pavel
 * Date: Jul 26, 2009
 * Time: 12:01:37 AM
 */
public abstract class KVSOperationBase implements Command, Intializible {
    @Link
    public ALinker aLinker;
    protected StatementFactory keyFactory;
    protected KeyValueStore<String, Object> keyValueStore;

    public void init(AnnotatedElement element, String keyTemplate) {
        if (keyTemplate != null) {
            try {
                keyFactory = aLinker.create(StatementFactory.class);
                keyFactory.init(element, keyTemplate);
            } catch (StatementFactoryException e) {
                throw new KVSException("Failed to parse key template '" + keyTemplate + "'");

            }
        }
        final Annotation[] methodAnnotations = element.getAnnotations();
        final Annotation[] classAnnotations = element.getClass().getAnnotations();
        Annotation[] annotations = new Annotation[methodAnnotations.length + classAnnotations.length];
        System.arraycopy(methodAnnotations, 0, annotations, 0, methodAnnotations.length);
        System.arraycopy(classAnnotations, 0, annotations, methodAnnotations.length, classAnnotations.length);
        final Context<KeyValueStore> context = new Context<KeyValueStore>(KeyValueStore.class, annotations, element, 0);
        //noinspection unchecked
        keyValueStore = aLinker.create(context);
    }

}