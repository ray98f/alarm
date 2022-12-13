package com.zte.alarm.server.runner;

import com.zte.alarm.core.codec.gjkz.GjkzCommand;
import com.zte.alarm.core.service.north.SocketType;
import com.zte.alarm.core.service.north.server.Server;
import com.zte.alarm.server.listener.EchoExceptionEventListener;
import com.zte.alarm.server.listener.EchoMessageEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author wshmang@163.com
 * @date 2021/3/22 15:46
 */
@Component
public class ServerRunner implements CommandLineRunner {

    @Value("${sys.port}")
    private int port;

    private Server server;

    @Autowired
    private EchoExceptionEventListener echoExceptionEventListener;

    @Autowired
    private EchoMessageEventListener echoMessageEventListener;

    @Override
    public void run(String... args) {
        server = new Server();
        server.setPort(port);
        server.setOpenExecutor(true);
        server.setSocketType(SocketType.GJKZ);
        server.addEventListener(echoExceptionEventListener);
        server.addEventListener(echoMessageEventListener);
        server.setCommand(new GjkzCommand());
        server.bind();
    }

    public Server getServer() {
        return this.server;
    }
}
