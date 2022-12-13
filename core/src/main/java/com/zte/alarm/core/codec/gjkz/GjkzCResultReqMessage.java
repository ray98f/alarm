package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/11 10:00
 */
public class GjkzCResultReqMessage extends GjkzMessage {

    public GjkzCResultReqMessage(GjkzFixedHeader gjkzFixedHeader, GjkzCResultReqVariableHeader variableHeader, GjkzCResultReqPayload payload) {
        super(gjkzFixedHeader, variableHeader, payload);
    }

    @Override
    public GjkzCResultReqVariableHeader getVariableHeader() {
        return (GjkzCResultReqVariableHeader)super.getVariableHeader();
    }

    @Override
    public GjkzCResultReqPayload getPayload() {
        return (GjkzCResultReqPayload)super.getPayload();
    }
}
