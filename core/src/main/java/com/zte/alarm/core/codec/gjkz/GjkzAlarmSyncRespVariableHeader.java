package com.zte.alarm.core.codec.gjkz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 10:42
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzAlarmSyncRespVariableHeader {
    /**
     * 告警总数
     */
    private int totalNumberOfAlarms;

    /**
     * 本包告警数
     */
    private int  numberOfAlarmsInThisPacket;

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
}
