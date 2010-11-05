package com.sf.ddao.factory.param;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.inject.Link;

import java.lang.reflect.AnnotatedElement;

/**
 * Date: Oct 27, 2009
 * Time: 3:34:24 PM
 */
public class JoinListParameterService implements ParameterService {
    public static final String FUNC_NAME = "joinList";
    @Link
    public ALinker aLinker;

    public void register(ParameterFactory parameterFactory) {
        parameterFactory.register(FUNC_NAME, this);
    }

    public Parameter create(AnnotatedElement element, String funcName, String param) {
        final JoinListParameter res = aLinker.create(JoinListParameter.class);
        res.init(element, param);
        return res;
    }
}
