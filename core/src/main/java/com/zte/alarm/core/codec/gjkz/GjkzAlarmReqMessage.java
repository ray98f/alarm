package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:04
 */
public class GjkzAlarmReqMessage extends GjkzMessage{
    public GjkzAlarmReqMessage(GjkzFixedHeader gjkzFixedHeader,GjkzAlarmReqVariableHeader variableHeader,GjkzAlarmReqPayload payload) {
        super(gjkzFixedHeader,variableHeader,payload);
    }

    @Override
    public GjkzAlarmReqVariableHeader getVariableHeader() {
        return (GjkzAlarmReqVariableHeader)super.getVariableHeader();
    }

    @Override
    public GjkzAlarmReqPayload getPayload() {
        return (GjkzAlarmReqPayload)super.getPayload();
    }
}
