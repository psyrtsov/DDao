package com.sf.ddao.factory.param;

import java.lang.reflect.AnnotatedElement;

/**
 * Date: Oct 27, 2009
 * Time: 2:16:25 PM
 */
public interface ParameterService {
    void register(ParameterFactory parameterFactory);

    Parameter create(AnnotatedElement element, String funcName, String paramName);
}
