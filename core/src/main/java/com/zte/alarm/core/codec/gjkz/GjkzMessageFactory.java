package com.zte.alarm.core.codec.gjkz;


import com.zte.alarm.core.codec.DecoderResult;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:10
 */
public class GjkzMessageFactory {

    public static GjkzMessage newMessage(GjkzFixedHeader gjkzFixedHeader, Object variableHeader, Object payload) {
        switch (gjkzFixedHeader.messageType()) {
            case CONNECT:
                return new GjkzConnectMessage(gjkzFixedHeader, (GjkzConnectVariableHeader) variableHeader, (GjkzConnectPayload) payload);
            case CONNACK:
                return new GjkzConnAckMessage(gjkzFixedHeader, (GjkzConnAckVariableHeader) variableHeader);
            case ALARMSYNCRESP:
                return new GjkzAlarmSyncRespMessage(gjkzFixedHeader, (GjkzAlarmSyncRespVariableHeader) variableHeader, (GjkzAlarmSyncRespPayload) payload);
            case ALARMSYNCREQ:
                return new GjkzAlarmSyncReqMessage(gjkzFixedHeader, (GjkzAlarmSyncReqVariableHeader) variableHeader, (GjkzAlarmSyncReqPayload) payload);
            case ALARMREQ:
                return new GjkzAlarmReqMessage(gjkzFixedHeader, (GjkzAlarmReqVariableHeader) variableHeader, (GjkzAlarmReqPayload) payload);
            case ALARMRESP:
                return new GjkzAlarmRespMessage(gjkzFixedHeader, (GjkzAlarmRespVariableHeader) variableHeader);
            case CONTROLREQ:
                return new GjkzControlReqMessage(gjkzFixedHeader, (GjkzControlReqVariableHeader) variableHeader, (GjkzControlReqPayload) payload);
            case CONTROLRESP:
                return new GjkzControlRespMessage(gjkzFixedHeader, (GjkzControlRespVariableHeader) variableHeader);
            case CRESULTREQ:
                return new GjkzCResultReqMessage(gjkzFixedHeader, (GjkzCResultReqVariableHeader) variableHeader, (GjkzCResultReqPayload) payload);
            case CRESULTRESP:
                return new GjkzCResultRespMessage(gjkzFixedHeader, (GjkzCResultRespVariableHeader) variableHeader);
            case READREQ:
                return new GjkzReadReqMessage(gjkzFixedHeader, (GjkzReadReqVariableHeader) variableHeader, (GjkzReadReqPayload) payload);
            case READRESP:
                return new GjkzReadRespMessage(gjkzFixedHeader, (GjkzReadRespVariableHeader) variableHeader, (GjkzReadRespPayload) payload);
            case PINGREQ:
            case PINGRESP:
            case DISCONNECT:
                return new GjkzMessage(gjkzFixedHeader);
            default:
                throw new IllegalArgumentException("未知的消息类型: " + gjkzFixedHeader.messageType());
        }
    }

    public static GjkzMessage newInvalidMessage(Throwable cause) {
        return new GjkzMessage((GjkzFixedHeader) null, (Object) null, (Object) null, DecoderResult.failure(cause));
    }

    public static GjkzMessage newInvalidMessage(GjkzFixedHeader gjkzFixedHeader, Object variableHeader, Throwable cause) {
        return new GjkzMessage(gjkzFixedHeader, variableHeader, (Object) null, DecoderResult.failure(cause));
    }

    private GjkzMessageFactory() {
    }
}
