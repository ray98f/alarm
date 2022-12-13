package com.zte.alarm.server.listener;

import org.junit.jupiter.api.Test;

/**
 * @author wshmang@163.com
 * @date 2021/3/30 15:43
 */
class EchoMessageEventListenerTest {

    @Test
    void alarmSyncResp() {

        writeVariableLengthInt(128);
        System.out.println();
        writeVariableLengthInt(16383);
        System.out.println();
        writeVariableLengthInt(151);
        System.out.println();


    }

    private static void writeVariableLengthInt(int num) {
        do {
            int digit = num % 128;
            num /= 128;
            if (num > 0) {
                digit |= 128;
            }

            System.out.print(digit);
            System.out.print(" ");
        } while (num > 0);
    }
}