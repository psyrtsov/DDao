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

package com.sf.ddao.alinker.initializer;
/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 4:45:38 PM
 */

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.Context;
import com.sf.ddao.alinker.Initializer;
import junit.framework.TestCase;

public class InitializerTest extends TestCase {
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

    public void testGetInitializers() throws Exception {
        final UseInitializerTest useInitializerTest = aLinker.create(UseInitializerTest.class, null);
        assertEquals(33, useInitializerTest.getValue());
    }

    @UseInitializer(TestInitializer.class)
    public static class UseInitializerTest {
        private int value;

        public void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class TestInitializer implements Initializer<UseInitializerTest> {
        public void init(ALinker aLinker, Context<UseInitializerTest> ctx, UseInitializerTest subj) throws InitializerException {
            subj.setValue(33);
        }
    }
}