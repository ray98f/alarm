package com.zte.alarm.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 10:43
 */
@Data
@AllArgsConstructor
public class SnmpAlarm {
    /**
     * 线路编号
     */
    private int lineCode;
    /**
     * 系统编号
     */
    private int systemCode;
    /**
     * 是否清除告警 false 正常告警  true 消除告警
     */
    private boolean cleared;
    /**
     * 告警位置名称
     */
    private String alarmManagedObjectInstanceName;
    /**
     * 告警特殊原因
     */
    private String alarmSpecificProblem;
    /**
     * 告警码
     */
    private String emsAlarmCode;
    /**
     * 网元类型
     */
    private String alarmNetype;

    /**
     * 告警时间
     */
    private LocalDateTime alarmTime;

    /**
     * 附加信息
     */
    private List<AlarmMessage> messages;
}
