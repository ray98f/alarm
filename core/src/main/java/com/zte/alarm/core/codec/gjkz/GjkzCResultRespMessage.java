package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:32
 */
public class GjkzCResultRespMessage extends GjkzMessage{
    public GjkzCResultRespMessage(GjkzFixedHeader gjkzFixedHeader, GjkzCResultRespVariableHeader variableHeader) {
        super(gjkzFixedHeader,variableHeader);
    }
    @Override
    public GjkzCResultRespVariableHeader getVariableHeader() {
        return (GjkzCResultRespVariableHeader)super.getVariableHeader();
    }
}
