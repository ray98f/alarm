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
        String RecoverAlarm = ".1.3.6.1.4.1.3902.4101.1.4.1.2";
        /**
         * 告警生成
         */
        String CreateAlarm = ".1.3.6.1.4.1.3902.4101.1.4.1.1";
        /**
         * 心跳
         */
        String Heart = ".1.3.6.1.6.3.1.1.5.1";
    }

    public interface Alarm {

        String AlarmCodeName = ".1.3.6.1.4.1.3902.4101.1.3.1.14";
        /**
         * 告警码
         */
        String AlarmCode = ".1.3.6.1.4.1.3902.4101.1.3.1.11";
        /**
         * 告警Id
         */
        String AlarmId = ".1.3.6.1.4.1.3902.4101.1.3.1.1";
        /**
         * 网元类型
         */
        String AlarmManagedObjectInstanceName = ".1.3.6.1.4.1.3902.4101.1.3.1.2";
        /**
         * 告警特殊原因
         */
        String AlarmSpecificProblem = ".1.3.6.1.4.1.3902.4101.1.3.1.7";

        /**
         * 网元类型
         */
        String AlarmNetType = ".1.3.6.1.4.1.3902.4101.1.3.1.12";
        /**
         * 告警注释
         */
        String AlarmComment = ".1.3.6.1.4.1.3902.4101.1.3.1.10";
        /**
         * 创建日期前缀
         */
        String AlarmCreateTimePrefix = "1.3.6.1.4.1.3902.4101.1.3.1.3";
    }

}
