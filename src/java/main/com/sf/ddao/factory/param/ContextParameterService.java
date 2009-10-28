package com.sf.ddao.factory.param;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.inject.Inject;

import java.lang.reflect.AnnotatedElement;

/**
 * Date: Oct 27, 2009
 * Time: 3:34:24 PM
 */
public class ContextParameterService implements ParameterService {
    public static final String FUNC_NAME = "ctx";
    @Inject
    public ALinker aLinker;

    public void register(ParameterFactory parameterFactory) {
        parameterFactory.register(FUNC_NAME, this);
    }

    public Parameter create(AnnotatedElement element, String funcName, String paramName) {
        final ContextParameter res = aLinker.create(ContextParameter.class);
        res.init(element, paramName);
        return res;
    }
}
