package com.sf.ddao.alinker.factory;

import com.sf.ddao.alinker.ALinker;

/**
 * Created by pavel
 * Date: Jul 25, 2009
 * Time: 12:06:52 PM
 */
public interface FactoryService {
    void register(ALinker aLinker, DefaultFactoryManager defaultFactoryManager);
}
