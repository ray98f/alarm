package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.pojo.Alarm;
import com.zte.alarm.core.pojo.AlarmList;
import com.zte.alarm.core.pojo.AlarmMessage;
import com.zte.alarm.core.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 9:12
 */

@Slf4j
public class GjkzEncoderTest {


    @Test
    public void 告警同步响应_正常1() {
        AlarmList alarmList = new AlarmList();
        LocalDateTime now = LocalDateTime.of(2021, 3, 12, 9, 2, 1);

        Alarm alarm1 = new Alarm(now, 1, 2, 3, 4, 5, false, 6, null);

        alarmList.add(alarm1);

        Alarm alarm2 = new Alarm(now, 2, 3, 4, 5, 6, true, 7, null);

        alarmList.add(alarm2);

        List<AlarmMessage> alarmMessageList = new ArrayList<>();
        alarmMessageList.add(new AlarmMessage("title1", "content1"));
        alarmMessageList.add(new AlarmMessage("title2", "content2"));
        Alarm alarm3 = new Alarm(now, 4, 5, 6, 7, 8, false, 9, alarmMessageList);

        alarmList.add(alarm3);

        GjkzAlarmSyncRespMessage message = new GjkzAlarmSyncRespMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMSYNCRESP, 0),
                new GjkzAlarmSyncRespVariableHeader(6, 3, 2, 1, 1),
                new GjkzAlarmSyncRespPayload(alarmList)
        );
        ByteBuf buf = GjkzEncoder.encodeAlarmSyncRespMessage(message);
        log.info("告警同步响应_正常1:{}", CommonUtil.bytesToHexString(buf.array()));
    }


    @Test
    public void 告警同步响应_正常2() {
        AlarmList alarmList = new AlarmList();
        LocalDateTime now = LocalDateTime.of(2021, 3, 12, 9, 2, 1);

        Alarm alarm1 = new Alarm(now, 1, 2, 3, 4, 5, false, 6, null);

        alarmList.add(alarm1);

        Alarm alarm2 = new Alarm(now, 2, 3, 4, 5, 6, true, 7, null);

        alarmList.add(alarm2);

        List<AlarmMessage> alarmMessageList = new ArrayList<>();
        alarmMessageList.add(new AlarmMessage("title1", "content1"));
        alarmMessageList.add(new AlarmMessage("title2", "content2"));
        Alarm alarm3 = new Alarm(now, 4, 5, 6, 7, 8, false, 9, alarmMessageList);

        alarmList.add(alarm3);

        GjkzAlarmSyncRespMessage message = new GjkzAlarmSyncRespMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMSYNCRESP, 0),
                new GjkzAlarmSyncRespVariableHeader(6, 3, 2, 2, 2),
                new GjkzAlarmSyncRespPayload(alarmList)
        );
        ByteBuf buf = GjkzEncoder.encodeAlarmSyncRespMessage(message);
        log.info("告警同步响应_正常2:{}", CommonUtil.bytesToHexString(buf.array()));
    }


    @Test
    public void 告警同步响应_无告警信息() {

        GjkzAlarmSyncRespMessage message = new GjkzAlarmSyncRespMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMSYNCRESP, 0),
                new GjkzAlarmSyncRespVariableHeader(0, 0, 0, 0, 1),
                new GjkzAlarmSyncRespPayload(null)
        );
        ByteBuf buf = GjkzEncoder.encodeAlarmSyncRespMessage(message);
        log.info("告警同步响应_无告警信息:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 告警请求_不包含附加信息() {
        LocalDateTime now = LocalDateTime.of(2021, 3, 31, 15, 2, 1);
        Alarm alarm1 = new Alarm(now, 1, 2, 1, 1, 1, false, 5050, null);


        GjkzAlarmReqMessage message = new GjkzAlarmReqMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMREQ, 0),
                new GjkzAlarmReqVariableHeader(1, alarm1),
                new GjkzAlarmReqPayload(null)
        );
        ByteBuf buf = GjkzEncoder.encodeAlarmReqMessage(message);
        log.info("告警请求_不包含附加信息:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 告警请求_包含附加信息() {

        List<AlarmMessage> alarmMessageList = new ArrayList<>();
        alarmMessageList.add(new AlarmMessage("title1", "content1"));
        alarmMessageList.add(new AlarmMessage("title2", "content2"));

        LocalDateTime now = LocalDateTime.of(2021, 3, 12, 9, 2, 1);
        Alarm alarm1 = new Alarm(now, 2, 2, 1, 1, 1, false, 5050, alarmMessageList);


        GjkzAlarmReqMessage message = new GjkzAlarmReqMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMREQ, 0),
                new GjkzAlarmReqVariableHeader(1, alarm1),
                new GjkzAlarmReqPayload(alarm1.getAlarmMessageList())
        );

        ByteBuf buf = GjkzEncoder.encodeAlarmReqMessage(message);
        log.info("告警请求_包含附加信息:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 控制响应() {
        GjkzControlRespMessage message = new GjkzControlRespMessage(
                new GjkzFixedHeader(GjkzMessageType.CONTROLRESP, 0),
                new GjkzControlRespVariableHeader(1)
        );
        ByteBuf buf = GjkzEncoder.encodeControlRespMessage(message);
        log.info("控制响应:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 控制结果请求() {
        GjkzCResultReqMessage message = new GjkzCResultReqMessage(
                new GjkzFixedHeader(GjkzMessageType.CRESULTREQ, 0),
                new GjkzCResultReqVariableHeader(1),
                new GjkzCResultReqPayload(1)
        );
        ByteBuf buf = GjkzEncoder.encodeCResultReqMessage(message);
        log.info("控制结果请求:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 读响应() {
        byte[] value = new byte[2];
        value[0] = 0;
        value[1] = 1;
        GjkzReadRespMessage message = new GjkzReadRespMessage(
                new GjkzFixedHeader(GjkzMessageType.READRESP, 0),
                new GjkzReadRespVariableHeader(1),
                new GjkzReadRespPayload(value)
        );
        ByteBuf buf = GjkzEncoder.encodeReadRespMessage(message);
        log.info("读响应:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 断开连接() {
        GjkzMessage message = new GjkzMessage(
                new GjkzFixedHeader(GjkzMessageType.DISCONNECT, 0)
        );
        ByteBuf buf = GjkzEncoder.encodeDisconnectMessage(message);
        log.info("断开连接:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 心跳请求() {
        GjkzMessage message = new GjkzMessage(
                new GjkzFixedHeader(GjkzMessageType.PINGREQ, 0)
        );
        ByteBuf buf = GjkzEncoder.encodePingReqMessage(message);
        log.info("心跳请求:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 心跳响应() {
        GjkzMessage message = new GjkzMessage(
                new GjkzFixedHeader(GjkzMessageType.PINGRESP, 0)
        );
        ByteBuf buf = GjkzEncoder.encodePingRespMessage(message);
        log.info("心跳响应:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 连接请求() {

        GjkzConnectMessage message = new GjkzConnectMessage(
                new GjkzFixedHeader(GjkzMessageType.CONNECT, 0),
                new GjkzConnectVariableHeader("GJKZ", 1, false, false,false,false,false,false,false,false, 3),
                new GjkzConnectPayload("client_1_2")
        );
        ByteBuf buf = GjkzEncoder.encodeConnectMessage(message);
        log.info("连接请求:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 连接请求_包含账号密码() {
        GjkzConnectMessage message = new GjkzConnectMessage(
                new GjkzFixedHeader(GjkzMessageType.CONNECT, 0),
                new GjkzConnectVariableHeader("GJKZ", 1, true, true, false,false,false,false,false,false,3),
                new GjkzConnectPayload("client_1_2", "userName", new byte[]{1, 2, 3})
        );
        ByteBuf buf = GjkzEncoder.encodeConnectMessage(message);
        log.info("连接请求_包含账号密码:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 连接响应() {
        GjkzConnAckMessage message = new GjkzConnAckMessage(
                new GjkzFixedHeader(GjkzMessageType.CONNACK, 0),
                new GjkzConnAckVariableHeader(GjkzConnectReturnCode.CONNECTION_ACCEPTED)
        );
        ByteBuf buf = GjkzEncoder.encodeConnAckMessage(message);
        log.info("连接响应:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 告警同步请求() {
        GjkzAlarmSyncReqMessage message = new GjkzAlarmSyncReqMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMSYNCREQ, 0)
        );
        ByteBuf buf = GjkzEncoder.encodeAlarmSyncReqMessage(message);
        log.info("告警同步请求:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 告警响应() {
        GjkzAlarmRespMessage message = new GjkzAlarmRespMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMRESP, 0),
                new GjkzAlarmRespVariableHeader(0)
        );
        ByteBuf buf = GjkzEncoder.encodeAlarmRespMessage(message);
        log.info("告警响应:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 控制请求() {
        GjkzControlReqMessage message = new GjkzControlReqMessage(
                new GjkzFixedHeader(GjkzMessageType.CONTROLREQ, 0),
                new GjkzControlReqVariableHeader(1, 2, 3, 4),
                new GjkzControlReqPayload(
                        new byte[]{0, 1},
                        new byte[]{0, 2}
                )
        );
        ByteBuf buf = GjkzEncoder.encodeControlReqMessage(message);
        log.info("控制请求:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 控制结果响应() {
        GjkzCResultRespMessage message = new GjkzCResultRespMessage(
                new GjkzFixedHeader(GjkzMessageType.CRESULTRESP, 0),
                new GjkzCResultRespVariableHeader(1)
        );
        ByteBuf buf = GjkzEncoder.encodeCResultRespMessage(message);
        log.info("控制结果响应:{}", CommonUtil.bytesToHexString(buf.array()));
    }

    @Test
    public void 读请求() {
        GjkzReadReqMessage message = new GjkzReadReqMessage(
                new GjkzFixedHeader(GjkzMessageType.READREQ, 0),
                new GjkzReadReqVariableHeader(1, 2, 3, 4),
                new GjkzReadReqPayload(new byte[]{0, 1})
        );
        ByteBuf buf = GjkzEncoder.encodeReadeReqMessage(message);
        log.info("读请求:{}", CommonUtil.bytesToHexString(buf.array()));
    }
}