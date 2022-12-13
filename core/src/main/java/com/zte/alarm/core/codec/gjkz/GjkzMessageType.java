package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 15:33
 */
public enum GjkzMessageType {
    /**
     * 消息类型
     */
    CONNECT(1),
    CONNACK(2),
    PINGREQ(3),
    PINGRESP(4),
    DISCONNECT(5),
    ALARMSYNCREQ(6),
    ALARMSYNCRESP(7),
    ALARMREQ(8),
    ALARMRESP(9),
    CONTROLREQ(10),
    CONTROLRESP(11),
    CRESULTREQ(12),
    CRESULTRESP(13),
    READREQ(14),
    READRESP(15);

    private final int value;

    private GjkzMessageType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static GjkzMessageType valueOf(int type) {
        GjkzMessageType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            GjkzMessageType t = var1[var3];
            if (t.value == type) {
                return t;
            }
        }

        throw new IllegalArgumentException("未知的消息类型: " + type);
    }
}
