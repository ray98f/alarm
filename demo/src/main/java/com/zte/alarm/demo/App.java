package com.zte.alarm.demo;

import com.zte.alarm.core.service.north.SocketType;
import com.zte.alarm.core.service.north.server.Server;
import com.zte.alarm.demo.listener.EchoExceptionEventListener;
import com.zte.alarm.demo.listener.EchoMessageEventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 9:42
 */
@Slf4j
public class App {
    public static void main(String[] args) throws InterruptedException {
        int port = 8000;
        Server server = new Server();
        server.setPort(port);
        server.setOpenExecutor(true);
        server.setSocketType(SocketType.GJKZ);
        server.addEventListener(new EchoMessageEventListener());
        server.addEventListener(new EchoExceptionEventListener());
        server.bind();

        while (true) {
            Thread.sleep(1000L);
        }
    }
}

