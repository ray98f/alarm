package com.zte.snmp.constant;

public final class UMEConstants {

    /**
     * 时间
     */
    public static final String KeyTime = ".1.3.6.1.2.1.1.3.0";
    /**
     * 类型
     */
    public static final String KeyType = ".1.3.6.1.6.3.1.1.4.1.0";


    public static final String MessageId = "1.3.6.1.4.1.3902.4101.1.1.2";
    public static final String LastMessageId = "1.3.6.1.4.1.3902.4101.1.1.4";


    /**
     * 告警级别
     */
    public static final String AlarmLevel = ".1.3.6.1.4.1.3902.4101.1.3.1.6.1";

    public static final String AlarmType = "1.3.6.1.4.1.3902.4101.1.3.1.4.1";
    public static final String AlarmId = "1.3.6.1.4.1.3902.4101.1.3.1.1.1";
    public static final String AlarmIdU31 = "1.3.6.1.4.1.3902.4101.1.3.1.24.1";
    public static final String AlarmLocation = "1.3.6.1.4.1.3902.4101.1.3.1.15.1";
    /**
     * 同步告警
     */
    public static final String AlarmTypeSync = ".1.3.6.1.4.1.3902.4101.1.4.1.";

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

        String AlarmCodeName = ".1.3.6.1.4.1.3902.4101.1.3.1.14";
        /**
         * 告警码
         */
        String AlarmCode = ".1.3.6.1.4.1.3902.4101.1.3.1.11";
        String AlarmId = ".1.3.6.1.4.1.3902.4101.1.3.1.24.1";
        /**
         * 告警位置名称
         */
        String AlarmManagedObjectInstanceName = ".1.3.6.1.4.1.3902.4101.1.3.1.15";
        String AlarmSpecificProblem = ".1.3.6.1.4.1.3902.4101.1.3.1.7";
        /**
         * 告警类型
         */
        String AlarmEventType = ".1.3.6.1.4.1.3902.4101.1.3.1.4";
        /**
         * 网元类型
         */
        String AlarmNetType = ".1.3.6.1.4.1.3902.4101.1.3.1.12";
        String AlarmComment = ".1.3.6.1.4.1.3902.4101.1.3.1.10";
        String AlarmCreateTimePrefix = "1.3.6.1.4.1.3902.4101.1.3.1.3";
    }

}
