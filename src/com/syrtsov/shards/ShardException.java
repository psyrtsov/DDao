package com.syrtsov.shards;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 22, 2008
 * Time: 9:31:38 AM
 */
public class ShardException extends RuntimeException {
    public ShardException() {
    }

    public ShardException(String message) {
        super(message);
    }

    public ShardException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShardException(Throwable cause) {
        super(cause);
    }
}
