package com.sf.ddao.ops;

import com.sf.ddao.IterableArg;
import com.sf.ddao.IterateSelect;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Inject;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.factory.StatementFactory;
import static com.sf.ddao.utils.Annotations.findParameterAnnotatedWith;
import org.apache.commons.chain.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Date: Feb 8, 2010
 * Time: 4:52:45 PM
 */
public class IterateSelectSqlOperation extends SelectSqlOperation {
    private int iterableArgIdx;
    private Class<? extends Collection> returnClass = null;

    @Inject
    public IterateSelectSqlOperation(StatementFactory statementFactory) {
        super(statementFactory);
    }

    public boolean execute(Context context) throws Exception {
        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        final Object[] args = callCtx.getArgs();
        final Object iterableArg = args[iterableArgIdx];
        Iterable iterable;
        if (iterableArg.getClass().isArray()) {
            iterable = new SingleUseIterableArrayWraper(iterableArg);
        } else {
            iterable = (Iterable) iterableArg;
        }
        Collection result = returnClass == null ? null : returnClass.newInstance();
        for (Object param : iterable) {
            // psdo: assigning value of wrong type is not good idea,
            // we might want to do somethnig using ParameterService
            args[iterableArgIdx] = param;
            super.execute(context);
            if (result != null) {
                Collection lastReturn = (Collection) callCtx.getLastReturn();
                //noinspection unchecked
                result.addAll(lastReturn);
            }
        }
        // restore old arg value
        args[iterableArgIdx] = iterable;
        callCtx.setLastReturn(result);
        return CONTINUE_PROCESSING;
    }

    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        Method method = (Method) element;
        iterableArgIdx = findParameterAnnotatedWith(method, IterableArg.class);
        if (iterableArgIdx < 0) {
            throw new UnsupportedOperationException("Method " + method +
                    " expected to have parameter annotated with " + IterableArg.class);
        }
        final Class<?> returnType = method.getReturnType();
        if (returnType != Void.TYPE) {
            if (Collection.class.isAssignableFrom(returnType)) {
                if (returnType.isInterface()) {
                    returnClass = ArrayList.class;
                } else {
                    //noinspection unchecked
                    returnClass = (Class<? extends Collection>) returnType;
                }
            } else {
                throw new UnsupportedOperationException("Return type for method " + method +
                        " expected to be void or Collection");
            }

        }
        IterateSelect selectSql = element.getAnnotation(IterateSelect.class);
        init(element, selectSql.value());
    }

    private class SingleUseIterableArrayWraper implements Iterable, Iterator {
        private final Object arrayObj;
        private final int len;
        private int idx = 0;


        public SingleUseIterableArrayWraper(Object arrayObj) {
            this.arrayObj = arrayObj;
            this.len = Array.getLength(arrayObj);
        }

        public Iterator iterator() {
            return this;
        }

        public boolean hasNext() {
            return idx < len;
        }

        public Object next() {
            return Array.get(arrayObj, idx++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
