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

package com.sf.ddao.crud;

/**
 * should be implemented by bean that can have "dirty" state , that is state when it has changes
 * that have to be saved to DB
 */
public interface DirtyableBean {
    /**
     * invoked by annotation @see com.sf.ddao.crud.CheckIfBeanIsDirty to define is bean is dirty and
     * we should go ahead with execution of updates for this data
     *
     * @return true if bean is dirty and update has to proceed
     */
    boolean beanIsDirty();

    /**
     * clean dirty state from this bean
     */
    void cleanDirtyBean();
}
