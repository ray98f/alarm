package com.zte.alarm.core.service.north.server;

import com.zte.alarm.core.service.north.SocketType;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 9:14
 */
public class ServerTest {

    public static void main(String[] args) throws InterruptedException {

        writeVariableLengthInt(709);

        Server server = new Server();
        server.setPort(8000);
        server.setOpenExecutor(true);
        server.setSocketType(SocketType.GJKZ);
        server.addEventListener(new EchoMessageEventListener());
        server.bind();


        while (true) {

            Thread.sleep(1000L);
        }
    }

    private static void writeVariableLengthInt(int num) {
        do {
            int digit = num % 128;
            num /= 128;
            if (num > 0) {
                digit |= 128;
            }

            System.out.println(digit);
        } while (num > 0);
    }
}