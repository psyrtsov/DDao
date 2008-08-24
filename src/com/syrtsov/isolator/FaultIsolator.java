package com.syrtsov.isolator;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/**
 * User: Pavel Syrtsov
 * Date: Aug 24, 2008
 * Time: 10:37:56 AM
 * psdo: provide comments for class ${CLASSNAME}
 */
public class FaultIsolator {
    private long startTime = System.currentTimeMillis();
    private List<Future<?>> futureList = new ArrayList<Future<?>>();
    private ExecutorService executor;
    private final FaultIsolatorManager faultIsolatorManager;

    public FaultIsolator(ExecutorService executor, FaultIsolatorManager faultIsolatorManager) {
        this.executor = executor;
        this.faultIsolatorManager = faultIsolatorManager;
    }

    public boolean async(final Enum feature, HttpServletRequest httpServletRequest, final Runnable runnable) {
        final AtomicInteger featurePerActionCounter = getCounter(feature, httpServletRequest);
        featurePerActionCounter.getAndIncrement();
        if(!faultIsolatorManager.begin(feature)) {
            // note that featurePerActionCounter is not go to decrement
            // and feature data will be marked as unavailable
            return false;
        }
        Future<?> future = executor.submit(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                    featurePerActionCounter.getAndDecrement();
                } finally {
                    faultIsolatorManager.end(feature);                    
                }
            }
        });
        futureList.add(future);
        return true;
    }

    public void waitToComplete(long timeOut) throws ExecutionException, TimeoutException, InterruptedException {
        for (Future<?> future : futureList) {
            long timeLeft = System.currentTimeMillis() - startTime - timeOut;
            if (timeLeft <= 0L) {
                throw new TimeoutException();
            }
            future.get(timeLeft, TimeUnit.MILLISECONDS);
        }
    }

    public static AtomicInteger getCounter(Enum feature, HttpServletRequest httpServletRequest) {
        AtomicInteger res;
        String key = FaultIsolatorManager.class.getSimpleName() + feature.name();
        synchronized (httpServletRequest) {
            res = (AtomicInteger) httpServletRequest.getAttribute(key);
            if (res == null) {
                res = new AtomicInteger(0);
                httpServletRequest.setAttribute(key, res);
            }
        }
        return res;
    }

    public static boolean isOk(Enum feature, HttpServletRequest httpServletRequest) {
        AtomicInteger featurePerActionCounter = getCounter(feature, httpServletRequest);
        return featurePerActionCounter.get() == 0;
    }
}
