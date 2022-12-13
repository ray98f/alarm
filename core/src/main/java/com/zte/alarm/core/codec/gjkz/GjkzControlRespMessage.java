package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/11 10:00
 */
public class GjkzControlRespMessage extends GjkzMessage  {
    public GjkzControlRespMessage(GjkzFixedHeader gjkzFixedHeader, GjkzControlRespVariableHeader variableHeader) {
        super(gjkzFixedHeader, variableHeader);
    }

    @Override
    public GjkzControlRespVariableHeader getVariableHeader() {
        return (GjkzControlRespVariableHeader)super.getVariableHeader();
    }

}
