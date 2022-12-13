package com.zte.alarm.core.service.snmp;

import com.zte.alarm.core.pojo.SnmpAlarm;

import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/26 10:49
 */
public interface SnmpCommand {
    /**
     * 获取告警列表
     * @return
     */
    List<SnmpAlarm> getAlarmList();
}
