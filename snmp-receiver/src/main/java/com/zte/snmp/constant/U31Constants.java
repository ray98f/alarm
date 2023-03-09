package com.zte.snmp.constant;

public final class U31Constants {

    /**
     * 时间
     */
    public static final String KeyTime = ".1.3.6.1.2.1.1.3.0";
    /**
     * 类型
     */
    public static final String KeyType = ".1.3.6.1.6.3.1.1.4.1.0";

    public interface Type {

        /**
         * 告警恢复
         */
        String RecoverAlarm = "1.3.6.1.4.1.3902.4101.1.4.1.2";
        /**
         * 告警生成
         */
        String CreateAlarm = "1.3.6.1.4.1.3902.4101.1.4.1.1";
        /**
         * 心跳
         */
        String Heart = "1.3.6.1.4.1.3902.4101.4.2.1.1";
    }

    public interface Alarm {

        /**
         * 告警码
         */
        String AlarmCode = ".1.3.6.1.4.1.3902.4101.1.3.1.11";
        /**
         * 网元类型
         */
        String AlarmManagedObjectInstanceName = ".1.3.6.1.4.1.3902.4101.1.3.1.15";
        /**
         * 告警特殊原因
         */
        String AlarmSpecificProblem = ".1.3.6.1.4.1.3902.4101.1.3.1.7";

        /**
         * 网元类型
         */
        String AlarmEventType = ".1.3.6.1.4.1.3902.4101.1.3.1.4";
        /**
         * 创建日期前缀
         */
        String AlarmCreateTimePrefix = "1.3.6.1.4.1.3902.4101.1.3.1.3";

        /**
         * 网元名称
         */
        String AlarmMocObjectInstance = "1.3.6.1.4.1.3902.4101.1.3.1.26";

        /**
         * 告警确认状态
         * 1 表示确认，2 示未确认。
         */
        String AlarmAck = ".1.3.6.1.4.1.3902.4101.1.3.1.18";
    }

}
