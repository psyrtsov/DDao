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

package com.sf.ddao.alinker;
/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:22:56 PM
 */

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.alinker.factory.InjectValue;
import com.sf.ddao.alinker.factory.UseFactory;
import junit.framework.TestCase;

public class CtxBuilderTest extends TestCase {
    private static final String INJECTED_VALUE = "injectedValue";
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
        Context<MyInjectorTest> context = CtxBuilder.create(MyInjectorTest.class).add(UseFactory.class, MyFactory.class).get();
        MyInjectorTest testObj = aLinker.create(context);
        assertEquals(INJECTED_VALUE, testObj.injectedValue);
    }

    public static class MyFactory implements Factory {
        public Object create(ALinker aLinker, Context ctx) throws FactoryException {
            MyInjectorTest injectorTest = new MyInjectorTest();
            injectorTest.setInjectedValue(INJECTED_VALUE);
            return injectorTest;
        }
    }

    private static class MyInjectorTest {
        String injectedValue = null;

        public void setInjectedValue(@InjectValue(INJECTED_VALUE)String injectedValue) {
            this.injectedValue = injectedValue;
        }
    }
}