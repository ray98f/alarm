package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:32
 */
public class GjkzReadReqMessage extends GjkzMessage{
    public GjkzReadReqMessage(GjkzFixedHeader gjkzFixedHeader, GjkzReadReqVariableHeader variableHeader, GjkzReadReqPayload payload) {
        super(gjkzFixedHeader, variableHeader, payload);
    }
    @Override
    public GjkzReadReqVariableHeader getVariableHeader() {
        return (GjkzReadReqVariableHeader)super.getVariableHeader();
    }

    @Override
    public GjkzReadReqPayload getPayload() {
        return (GjkzReadReqPayload)super.getPayload();
    }
}
