package com.syrtsov.ddao.conn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 19, 2008
 * Time: 3:04:02 PM
 */
public class ConnectionHolderHelper implements InvocationHandler {
    private ThreadLocal<Connection> connectionOnHold = new ThreadLocal<Connection>();
    private Connection connProxy = null;

    public ConnectionHolderHelper() {
        connProxy = (Connection) Proxy.newProxyInstance(ConnectionHandlerHelper.class.getClassLoader(),
                new Class<?>[]{Connection.class}, this);
    }

    public void putConnectionOnHold(Connection connection) {
        Connection res = connectionOnHold.get();
        if (res == null) {
            connectionOnHold.set(connection);
        }
    }

    public Connection getConnectionProxy() {
        return connProxy;
    }

    public boolean hasConnectionOnHold() {
        return connectionOnHold.get() != null;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Connection conn = connectionOnHold.get();
        if ("close".equals(method.getName())) {
            connectionOnHold.remove();
        }
        return method.invoke(conn, args);
    }
}
