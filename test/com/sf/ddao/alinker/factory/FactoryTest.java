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

package com.sf.ddao.alinker.factory;
/**
 * Created-By: Pavel Syrtsov
 * Date: Apr 10, 2008
 * Time: 3:32:40 PM
 */

import com.sf.ddao.alinker.*;
import com.sf.ddao.alinker.inject.Link;
import junit.framework.TestCase;

import java.lang.reflect.InvocationHandler;

public class FactoryTest extends TestCase {
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

    public void testSimpleCreate() throws Exception {
        StringBuilder sb = aLinker.create(StringBuilder.class, null);
        assertNotNull(sb);
    }

    public void testInjectedConstructor() throws Exception {
        final InjectedConstructorTest injectedConstructorTest = aLinker.create(InjectedConstructorTest.class, null);
        assertNotNull(injectedConstructorTest);
        assertNotNull(injectedConstructorTest.sb);
        assertEquals("injectedString", injectedConstructorTest.injectedString);
    }

    public static class InjectedConstructorTest {
        public final StringBuilder sb;
        @InjectValue("injectedString")
        public String injectedString;

        @Link
        public InjectedConstructorTest(StringBuilder sb) {
            this.sb = sb;
        }
    }

    public void testUseFactory() throws Exception {
        final UseFactoryTest useFactoryTest = aLinker.create(UseFactoryTest.class, null);
        assertNotNull(useFactoryTest);
        assertEquals(useFactoryTest.getClass(), UseFactoryTestImpl.class);
    }

    @UseFactory(CustomFactory.class)
    public static interface UseFactoryTest {
    }

    private static class UseFactoryTestImpl implements UseFactoryTest {
    }

    public static class CustomFactory implements Factory<UseFactoryTest> {

        public UseFactoryTest create(ALinker nInjector, Context ctx) throws FactoryException {
            return new UseFactoryTestImpl();
        }
    }

    public void testCachingFactory() throws Exception {
        final CachingFactoryTest cachingFactoryTest1 = aLinker.create(CachingFactoryTest.class, null);
        assertNotNull(cachingFactoryTest1);
        assertEquals(cachingFactoryTest1.getClass(), CachingFactoryTestImpl.class);
        final CachingFactoryTest cachingFactoryTest2 = aLinker.create(CachingFactoryTest.class, null);
        assertNotNull(cachingFactoryTest2);
        assertEquals(cachingFactoryTest2.getClass(), CachingFactoryTestImpl.class);
        // should be same object
        assertTrue(cachingFactoryTest1 == cachingFactoryTest2);
    }

    @UseFactory(MyCachingFactory.class)
    public static interface CachingFactoryTest {
    }

    private static class CachingFactoryTestImpl implements CachingFactoryTest {
    }

    public static class MyCachingFactory implements CachingFactory<CachingFactoryTest> {
        private CachingFactoryTestImpl cachedObj;

        public CachingFactoryTest create(ALinker nInjector, Context ctx) throws FactoryException {
            cachedObj = new CachingFactoryTestImpl();
            return cachedObj;
        }

        public CachingFactoryTest getCachedObject(Context ctx) {
            return cachedObj;
        }
    }

    public void testErrorOnInterfaceInstantiation() throws Exception {
        try {
            // just use some random iface to check that it throws exception
            aLinker.create(InvocationHandler.class, null);
            fail("Expected exception");
        } catch (FactoryException ex) {
            // ignore
        }
    }
}