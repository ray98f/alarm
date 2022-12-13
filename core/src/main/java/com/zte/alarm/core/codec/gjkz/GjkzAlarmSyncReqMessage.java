package com.zte.alarm.core.codec.gjkz;

import lombok.Data;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:31
 */
@Data
public class GjkzAlarmSyncReqMessage extends GjkzMessage {

    public GjkzAlarmSyncReqMessage(GjkzFixedHeader gjkzFixedHeader) {
        super(gjkzFixedHeader);
    }

    public GjkzAlarmSyncReqMessage(GjkzFixedHeader gjkzFixedHeader, GjkzAlarmSyncReqVariableHeader variableHeader, GjkzAlarmSyncReqPayload payload) {
        super(gjkzFixedHeader, variableHeader, payload);
    }
}
