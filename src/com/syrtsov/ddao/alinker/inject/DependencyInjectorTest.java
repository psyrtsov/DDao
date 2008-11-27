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

package com.syrtsov.ddao.alinker.inject;
/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:22:56 PM
 */

import com.syrtsov.ddao.alinker.ALinker;
import com.syrtsov.ddao.alinker.factory.InjectValue;
import junit.framework.TestCase;

public class DependencyInjectorTest extends TestCase {
    private ALinker aLinker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        aLinker = new ALinker();
    }

    @Override
    protected void tearDown() throws Exception {
        aLinker = null;
        super.tearDown();
    }

    public void testMethodInjection() throws Exception {
        MyInjectorTest testObj = new MyInjectorTest();
        aLinker.init(testObj);
        assertEquals("injectedValue", testObj.injectedValue);
    }

    private class MyInjectorTest {
        String injectedValue = null;

        @Inject
        public void setInjectedValue(@InjectValue("injectedValue")String injectedValue) {
            this.injectedValue = injectedValue;
        }
    }
}