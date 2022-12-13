package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/11 10:00
 */
public class GjkzReadRespMessage extends GjkzMessage {
    public GjkzReadRespMessage(GjkzFixedHeader gjkzFixedHeader, GjkzReadRespVariableHeader variableHeader, GjkzReadRespPayload payload) {
        super(gjkzFixedHeader, variableHeader, payload);
    }
    @Override
    public GjkzReadRespVariableHeader getVariableHeader() {
        return (GjkzReadRespVariableHeader)super.getVariableHeader();
    }

    @Override
    public GjkzReadRespPayload getPayload() {
        return (GjkzReadRespPayload)super.getPayload();
    }
}
