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

package com.syrtsov.alinker;

import com.syrtsov.alinker.factory.FactoryTest;
import com.syrtsov.alinker.initializer.InitializerTest;
import com.syrtsov.alinker.inject.DependencyInjectorTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:59:11 PM
 */
public class AllTests {

    public static final TestSuite suite = new TestSuite("Tests for com.syrtsov.alinker");

    public static Test suite() {
        //$JUnit-BEGIN$
        suite.addTestSuite(FactoryTest.class);
        suite.addTestSuite(DependencyInjectorTest.class);
        suite.addTestSuite(InitializerTest.class);
        //$JUnit-END$
        return suite;
    }
}
