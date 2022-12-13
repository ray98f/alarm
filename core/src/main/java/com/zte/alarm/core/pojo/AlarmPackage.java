package com.zte.alarm.core.pojo;

import lombok.Data;

/**
 * @author wshmang@163.com
 * @date 2021/3/30 14:27
 */
@Data
public class AlarmPackage {

    /**
     * 告警总数
     */
    private int totalNumberOfAlarms;

    /**
     * 总包数
     */
    private int  totalNumberOfPackets;

    /**
     * 当前包序号
     */
    private int  currentPackageNumber;

    /**
     * 报文标识符
     */
    private int  messageIdentifier;


    /**
     * 告警信息
     */
    private AlarmList[] alarmList;
}
