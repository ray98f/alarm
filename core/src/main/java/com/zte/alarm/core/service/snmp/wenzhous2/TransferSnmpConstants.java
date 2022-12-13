package com.zte.alarm.core.service.snmp.wenzhous2;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 9:51
 */
public enum TransferSnmpConstants {

    /**
     * 对象
     */
    CURRENT_ALARM_TABLE("当前告警表格", "1.3.6.1.4.1.3902.4101.1.3", "1.1", "1.27"),
    REBUILD("告警列表重建Trap", "1.3.6.1.4.1.3902.4101.1.4.1.5"),
    HEARTBEAT("心跳", "1.3.6.1.4.1.3902.4101.4.2.1.1"),
    ALARM_NEW("告警消息", "1.3.6.1.4.1.3902.4101.1.4.1.1"),
    ALARM_CLEARED("告警恢复消息", "1.3.6.1.4.1.3902.4101.1.4.1.2"),

    /**
     * 字段
     */
    ALARM_EVENT_TIME("告警发生时间", "1.3.6.1.4.1.3902.4101.1.3.1.3"),
    SEND_NOTIFICATION_ID("本次发送序列号", "1.3.6.1.4.1.3902.4101.1.1.2"),
    LAST_SEND_NOTIFICATION_ID("上次发送序列号", "1.3.6.1.4.1.3902.4101.1.1.4"),
    SYSTEM_DN("系统DN", "1.3.6.1.4.1.3902.4101.1.1.3"),
    ALARM_CODE("告警码,即厂商告警码", "1.3.6.1.4.1.3902.4101.1.3.1.11"),
    ALARM_MANAGED_OBJECT_INSTANCE("网元位置", "1.3.6.1.4.1.3902.4101.1.3.1.2"),
    ALARM_EVENT_TYPE("告警类型", "1.3.6.1.4.1.3902.4101.1.3.1.4"),
    ALARM_PROBABLE_CAUSE("告警码中的北向原因码，即标准告警码", "1.3.6.1.4.1.3902.4101.1.3.1.5"),
    ALARM_PERCEIVED_SEVERITY("告警级别", "1.3.6.1.4.1.3902.4101.1.3.1.6"),
    ALARM_SPECIFIC_PROBLEM("告警特殊原因", "1.3.6.1.4.1.3902.4101.1.3.1.7"),
    ALARM_ADDITIONAL_TEXT("告警附加文本", "1.3.6.1.4.1.3902.4101.1.3.1.8"),
    ALARM_NETYPE("网元类型", "1.3.6.1.4.1.3902.4101.1.3.1.12"),
    ALARM_INDEX("索引", "1.3.6.1.4.1.3902.4101.1.3.1.9"),
    ALARM_ID("告警唯一标识", "1.3.6.1.4.1.3902.4101.1.3.1.1"),
    ALARM_CODE_NAME("告警码名称", "1.3.6.1.4.1.3902.4101.1.3.1.14"),
    ALARM_MANAGED_OBJECT_INSTANCE_NAME("告警位置名称", "1.3.6.1.4.1.3902.4101.1.3.1.15"),
    ALARM_SYSTEM_TYPE("告警系统类型", "1.3.6.1.4.1.3902.4101.1.3.1.16"),
    ALARM_NE_IP("网元IP", "1.3.6.1.4.1.3902.4101.1.3.1.17"),
    ALARM_ACK("告警确认状态", "1.3.6.1.4.1.3902.4101.1.3.1.18"),
    CLEI_CODE("设备资产号", "1.3.6.1.4.1.3902.4101.1.3.1.19"),
    TIME_ZONE_ID("告警上报时间时区ID", "1.3.6.1.4.1.3902.4101.1.3.1.20"),
    TIME_ZONE_OFFSET("告警上报时间时区偏移量", "1.3.6.1.4.1.3902.4101.1.3.1.21"),
    D_STSAVING("告警上报时间夏令时偏移量", "1.3.6.1.4.1.3902.4101.1.3.1.22"),
    AID("告警AID", "1.3.6.1.4.1.3902.4101.1.3.1.23"),
    ID("告警ID", "1.3.6.1.4.1.3902.4101.1.3.1.24"),
    ALARM_MOC_OBJECT_INSTANCE("网元名称", "1.3.6.1.4.1.3902.4101.1.3.1.26"),
    ALARM_COMMENT("告警注释", "1.3.6.1.4.1.3902.4101.1.3.1.10"),
    ALARM_OTHER_INFO("告警自定义属性", "1.3.6.1.4.1.3902.4101.1.3.1.25");

    private final String oid;
    private String lowerBound;
    private String upperBound;
    private final String name;

    TransferSnmpConstants(String name, String oid) {
        this.name = name;
        this.oid = oid;
    }

    TransferSnmpConstants(String name, String oid, String lowerBound, String upperBound) {
        this.name = name;
        this.oid = oid;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public static TransferSnmpConstants from(String oid) {
        for (TransferSnmpConstants telSnmpConstants : TransferSnmpConstants.values()) {
            if (oid.contains(telSnmpConstants.oid)) {
                return telSnmpConstants;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return oid;
    }

    public String getLowerBound() {
        return lowerBound;
    }

    public String getUpperBound() {
        return upperBound;
    }
}


