package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:31
 */
public class GjkzControlReqMessage extends GjkzMessage{

    public GjkzControlReqMessage(GjkzFixedHeader gjkzFixedHeader, GjkzControlReqVariableHeader variableHeader, GjkzControlReqPayload payload) {
        super(gjkzFixedHeader, variableHeader, payload);
    }

    @Override
    public GjkzControlReqVariableHeader getVariableHeader() {
        return (GjkzControlReqVariableHeader)super.getVariableHeader();
    }

    @Override
    public GjkzControlReqPayload getPayload() {
        return (GjkzControlReqPayload)super.getPayload();
    }

}
