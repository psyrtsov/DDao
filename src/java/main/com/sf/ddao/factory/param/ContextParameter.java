package com.sf.ddao.factory.param;

import org.apache.commons.chain.Context;

/**
 * Date: Oct 27, 2009
 * Time: 3:36:12 PM
 */
public class ContextParameter extends ParameterHelper {
    public Object extractData(Context context) throws ParameterException {
        return context.get(name);
    }
}
