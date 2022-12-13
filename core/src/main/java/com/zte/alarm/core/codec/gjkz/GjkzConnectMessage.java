package com.zte.alarm.core.codec.gjkz;



/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:01
 */
public class GjkzConnectMessage extends GjkzMessage {
    public GjkzConnectMessage(GjkzFixedHeader gjkzFixedHeader, GjkzConnectVariableHeader variableHeader, GjkzConnectPayload payload) {
        super(gjkzFixedHeader, variableHeader, payload);
    }

    @Override
    public GjkzConnectVariableHeader getVariableHeader() {
        return (GjkzConnectVariableHeader)super.getVariableHeader();
    }

    @Override
    public GjkzConnectPayload getPayload() {
        return (GjkzConnectPayload)super.getPayload();
    }
}
