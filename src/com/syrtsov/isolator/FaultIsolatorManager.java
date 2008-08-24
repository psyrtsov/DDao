package com.syrtsov.isolator;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

/**
 * User: Pavel Syrtsov
 * Date: Aug 23, 2008
 * Time: 11:14:22 PM
 * psdo: provide comments for class ${CLASSNAME}
 */
public class FaultIsolatorManager {
    // psdo: figure out how to inject this
    public static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();
    private ExecutorService executor = DEFAULT_EXECUTOR;
    private ConcurrentMap<Enum, AtomicInteger> featureQueueSizeMap = new ConcurrentHashMap<Enum, AtomicInteger>();

    public FaultIsolator createFaultIsolator() {
        return new FaultIsolator(executor, this);
    }

    public boolean begin(Enum feature) {
        getFeatureQueueSize(feature).getAndIncrement();
        return true;
    }

    private AtomicInteger getFeatureQueueSize(Enum feature) {
        AtomicInteger res;
        res = featureQueueSizeMap.get(feature);
        if (res == null) {
            featureQueueSizeMap.putIfAbsent(feature, new AtomicInteger());
            res = featureQueueSizeMap.get(feature);
        }
        return res;
    }


    public void end(Enum feature) {
        getFeatureQueueSize(feature).getAndDecrement();
    }
}
