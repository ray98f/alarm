package com.zte.alarm.core.codec.gjkz;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:31
 */
public class GjkzAlarmRespMessage extends GjkzMessage{
    public GjkzAlarmRespMessage(GjkzFixedHeader gjkzFixedHeader, GjkzAlarmRespVariableHeader variableHeader) {
        super(gjkzFixedHeader, variableHeader);
    }
    @Override
    public GjkzAlarmRespVariableHeader getVariableHeader() {
        return (GjkzAlarmRespVariableHeader)super.getVariableHeader();
    }
}
