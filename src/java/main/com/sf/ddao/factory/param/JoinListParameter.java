package com.sf.ddao.factory.param;

import org.apache.commons.chain.Context;

import java.util.Collection;
import java.util.List;

/**
 * Date: Oct 27, 2009
 * Time: 3:36:12 PM
 */
public class JoinListParameter extends DefaultParameter {
    public Object extractData(Context context) throws ParameterException {
        final List list = (List) super.extractData(context);
        return join(list);
    }

    public static String join(Collection list) {
        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
