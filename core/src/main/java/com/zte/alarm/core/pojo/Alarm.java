package com.zte.alarm.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 13:42
 */
@Getter
@AllArgsConstructor
@ToString
public class Alarm implements Serializable {
    /**
     * 报警时间
     */
    private LocalDateTime alarmTime;

    /**
     * 系统
     */
    private int system;

    /**
     * 线路
     */
    private int line;

    /**
     * 车站
     */
    private int station;

    /**
     * 设备
     */
    private int device;

    /**
     * 槽位
     */
    private int slot;

    /**
     * 故障恢复标识 true 恢复 false 故障
     */
    private boolean recovery;

    /**
     * 故障码
     */
    private int alarmCode;

    /**
     * 告警附加信息
     */
    private List<AlarmMessage> alarmMessageList;
}
