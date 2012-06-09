package com.sf.ddao.chain;

/**
 * Created by psyrtsov
 */
@SuppressWarnings("UnusedDeclaration")
public class InitializerException extends RuntimeException {
    public InitializerException() {
    }

    public InitializerException(String s) {
        super(s);
    }

    public InitializerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InitializerException(Throwable throwable) {
        super(throwable);
    }
}
