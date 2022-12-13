package com.zte.alarm.core.codec.gjkz;


/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:22
 */
public class GjkzConnAckMessage extends GjkzMessage {
    public GjkzConnAckMessage(GjkzFixedHeader gjkzFixedHeader, GjkzConnAckVariableHeader variableHeader) {
        super(gjkzFixedHeader, variableHeader);
    }

    @Override
    public GjkzConnAckVariableHeader getVariableHeader() {
        return (GjkzConnAckVariableHeader)super.getVariableHeader();
    }
}
