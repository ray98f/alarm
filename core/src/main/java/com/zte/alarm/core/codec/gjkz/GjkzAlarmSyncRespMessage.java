package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 10:19
 */
public class GjkzAlarmSyncRespMessage extends GjkzMessage {
    public GjkzAlarmSyncRespMessage(GjkzFixedHeader gjkzFixedHeader,GjkzAlarmSyncRespVariableHeader variableHeader,GjkzAlarmSyncRespPayload payload) {
        super(gjkzFixedHeader,variableHeader,payload);
    }

    @Override
    public GjkzAlarmSyncRespVariableHeader getVariableHeader() {
        return (GjkzAlarmSyncRespVariableHeader)super.getVariableHeader();
    }

    @Override
    public GjkzAlarmSyncRespPayload getPayload() {
        return (GjkzAlarmSyncRespPayload)super.getPayload();
    }
}
