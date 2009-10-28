package com.sf.ddao;

import java.sql.Connection;

/**
 * User: Pavel Syrtsov
 * Date: Sep 27, 2008
 * Time: 8:54:19 PM
 */
public interface TransactionableDao {
    @TransactionStarter
    Connection startTransaction();
}
