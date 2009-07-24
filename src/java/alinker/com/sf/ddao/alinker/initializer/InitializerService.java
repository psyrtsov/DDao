package com.sf.ddao.alinker.initializer;

import com.sf.ddao.alinker.ALinker;

/**
 * Created by pavel
 * Date: Jul 23, 2009
 * Time: 8:09:46 PM
 */
public interface InitializerService {
    void register(ALinker aLinker, DefaultInitializerManager defaultInitializerManager);
}
