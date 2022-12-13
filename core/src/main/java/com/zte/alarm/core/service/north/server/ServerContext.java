package com.zte.alarm.core.service.north.server;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 10:00
 */
public class ServerContext {

    private ServerContext() {
    }

    private static ServerContext instance = new ServerContext();

    public static ServerContext getContext() {
        return instance;
    }

    private Server server;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
