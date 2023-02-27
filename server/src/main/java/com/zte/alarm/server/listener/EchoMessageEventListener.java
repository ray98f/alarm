package com.zte.alarm.server.listener;

import com.zte.alarm.core.codec.gjkz.*;
import com.zte.alarm.core.listener.DefaultGjkzMessageEventListener;
import com.zte.alarm.core.pojo.AlarmList;
import com.zte.alarm.core.pojo.AlarmPackage;
import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.service.north.WrappedChannel;
import com.zte.alarm.core.util.CommonUtil;
import com.zte.alarm.server.queue.MessageSender;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 9:45
 */
@Slf4j
@Component
public class EchoMessageEventListener extends DefaultGjkzMessageEventListener {

    @Value("${sys.checklogin}")
    private boolean checkLogin;

    @Autowired
    MessageSender messageSender;

    @Override
    public void connect(WrappedChannel channel, GjkzConnectMessage msg) {
        channel.setLastHeartbeatTime(System.nanoTime());
        String clientId = msg.getPayload().getClientIdentifier();
        log.info("收到'{}-{}'连接请求:{}", channel.id().asShortText(), clientId, msg);
        int systemCode = 0;
        int lineCode = 0;

        GjkzVersion gjkzVersion = GjkzVersion.fromProtocolNameAndLevel(msg.getVariableHeader().getName(), (byte) msg.getVariableHeader().getVersion());
        if (gjkzVersion != GjkzVersion.GJKZ_1) {
            GjkzConnAckMessage okResp = (GjkzConnAckMessage) GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.CONNACK, 0),
                    new GjkzConnAckVariableHeader(GjkzConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION), null);
            channel.writeAndFlush(okResp);

            ByteBuf buf = GjkzEncoder.doEncode(okResp);
            log.info("回复客户端'{}'确认连接请求:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
            disConnect(channel, msg);
            return;
        }

        boolean hasError = false;
        try {
            if (!clientId.split("_")[0].equals("client")) {
                hasError = true;
            }
            systemCode = Integer.parseInt(clientId.split("_")[1]);
            lineCode = Integer.parseInt(clientId.split("_")[2]);
        } catch (Exception ex) {
            hasError = true;
        }
        if (hasError) {
            GjkzConnAckMessage okResp = (GjkzConnAckMessage) GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.CONNACK, 0),
                    new GjkzConnAckVariableHeader(GjkzConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED), null);
            channel.writeAndFlush(okResp);

            ByteBuf buf = GjkzEncoder.doEncode(okResp);
            log.info("回复客户端'{}'确认连接请求:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
            disConnect(channel, msg);
            return;
        }

        if (msg.getVariableHeader().isFlag0() || msg.getVariableHeader().isFlag1() || msg.getVariableHeader().isFlag2() || msg.getVariableHeader().isFlag3() || msg.getVariableHeader().isFlag4() || msg.getVariableHeader().isFlag5()) {
            disConnect(channel, msg);
            log.info("因连接标识错误断开连接");
            return;
        }

        if (!msg.getVariableHeader().isHasUserName() && !msg.getVariableHeader().isHasPassword()) {
            GjkzConnAckMessage okResp = (GjkzConnAckMessage) GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.CONNACK, 0),
                    new GjkzConnAckVariableHeader(GjkzConnectReturnCode.CONNECTION_ACCEPTED), null);
            channel.writeAndFlush(okResp);

            ByteBuf buf = GjkzEncoder.doEncode(okResp);
            log.info("回复客户端'{}'确认连接请求:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
        } else if (msg.getVariableHeader().isHasUserName() && msg.getVariableHeader().isHasPassword()) {
            //TODO 校验用户名密码
            GjkzConnAckMessage okResp = (GjkzConnAckMessage) GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.CONNACK, 0),
                    new GjkzConnAckVariableHeader(GjkzConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD), null);
            channel.writeAndFlush(okResp);

            ByteBuf buf = GjkzEncoder.doEncode(okResp);
            log.info("回复客户端'{}'确认连接请求:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
            disConnect(channel, msg);
            return;
        } else {
            GjkzConnAckMessage okResp = (GjkzConnAckMessage) GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.CONNACK, 0),
                    new GjkzConnAckVariableHeader(GjkzConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED), null);
            channel.writeAndFlush(okResp);

            ByteBuf buf = GjkzEncoder.doEncode(okResp);
            log.info("回复客户端'{}'确认连接请求:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
            disConnect(channel, msg);
            return;
        }

        channel.setClientId(clientId, systemCode, lineCode);

        sendAlarmSyncReq(channel);
    }

    private void sendAlarmSyncReq(WrappedChannel channel) {
        //发送同步请求命令
        GjkzAlarmSyncReqMessage gjkzAlarmSyncReqMessage = (GjkzAlarmSyncReqMessage) GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.ALARMSYNCREQ, 0),
                null, null);
        channel.writeAndFlush(gjkzAlarmSyncReqMessage);

        ByteBuf buf = GjkzEncoder.doEncode(gjkzAlarmSyncReqMessage);
        log.info("发送客户端'{}'告警同步请求:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
    }

    @Override
    public void pingReq(WrappedChannel channel, GjkzMessage message) {
        if (checkLogin) {
            if (channel.getClientId() == null || channel.getClientId().length() == 0) {
                log.info("用户未登录,关闭连接.");
                disConnect(channel, message);
                return;
            }
//            if (channel.getClientId().contains("12_2")) {
//                log.info("pingReq channel:" + channel.getClientId());
//            }
        }
        channel.setLastHeartbeatTime(System.nanoTime());
//        log.info("收到'{}'心跳请求:{}", channel.id().asShortText(), message);
        GjkzMessage pingResp = new GjkzMessage(new GjkzFixedHeader(GjkzMessageType.PINGRESP, 0));
        channel.writeAndFlush(pingResp);
        ByteBuf buf = GjkzEncoder.doEncode(pingResp);
//        log.info("回复客户端'{}'心跳响应:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));

        messageSender.sendHeartbeat(new ArrayList() {{
            add(new Heartbeat(channel.getLineCode(), channel.getSystemCode(), LocalDateTime.now()));
        }});
    }

    @Override
    public void alarmSyncResp(WrappedChannel channel, GjkzAlarmSyncRespMessage message) {
        if (checkLogin) {
            if (channel.getClientId() == null || channel.getClientId().length() == 0) {
                log.info("用户未登录,关闭连接.");
                disConnect(channel, message);
                return;
            }
        }
        messageSender.sendHeartbeat(new ArrayList() {{
            add(new Heartbeat(channel.getLineCode(), channel.getSystemCode(), LocalDateTime.now()));
        }});

        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'告警同步响应:{}", channel.id().asShortText(), message);


        if (message.getVariableHeader().getTotalNumberOfPackets() == 1) {
            messageSender.sendSyncAlarm(message.getPayload().getAlarmList());
            channel.setAlarmPackage(null);
        } else if (message.getVariableHeader().getTotalNumberOfPackets() > 1) {
            AlarmPackage alarmPackage = channel.getAlarmPackage();
            if (alarmPackage == null) {
                alarmPackage = new AlarmPackage();
                channel.setAlarmPackage(alarmPackage);

                alarmPackage.setTotalNumberOfAlarms(message.getVariableHeader().getTotalNumberOfAlarms());
                alarmPackage.setTotalNumberOfPackets(message.getVariableHeader().getTotalNumberOfPackets());
                alarmPackage.setAlarmList(new AlarmList[message.getVariableHeader().getTotalNumberOfPackets()]);

            }

            AlarmList[] alarmLists = alarmPackage.getAlarmList();
            alarmLists[message.getVariableHeader().getCurrentPackageNumber() - 1] = message.getPayload().getAlarmList();

            for (int i = 0; i < alarmLists.length; i++) {
                if (alarmLists[i] == null) {
                    // 包不全不处理
                    return;
                }
            }

            AlarmList result = new AlarmList();
            for (int i = 0; i < alarmLists.length; i++) {
                result.addAll(alarmLists[i]);
            }

            // 告警数量与包头告警总数不符
            if (result.size() != alarmPackage.getTotalNumberOfAlarms()) {
                log.warn("告警数量与包头告警总数不符,告警总数:{},包头告警数:{}", result.size(), alarmPackage.getTotalNumberOfAlarms());
                sendAlarmSyncReq(channel);
            } else {
                messageSender.sendSyncAlarm(result);
                channel.setAlarmPackage(null);
            }

        }

    }

    @Override
    public void alarmReq(WrappedChannel channel, GjkzAlarmReqMessage message) {
        if (checkLogin) {
            if (channel.getClientId() == null || channel.getClientId().length() == 0) {
                log.info("用户未登录,关闭连接.");
                disConnect(channel, message);
                return;
            }
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'告警请求:{}", channel.id().asShortText(), message);

        GjkzAlarmRespMessage gjkzAlarmRespMessage = new GjkzAlarmRespMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMRESP, 0),
                new GjkzAlarmRespVariableHeader(message.getVariableHeader().getMessageId()));
        channel.writeAndFlush(gjkzAlarmRespMessage);

        ByteBuf buf = GjkzEncoder.doEncode(gjkzAlarmRespMessage);
        log.info("回复客户端'{}'告警响应:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));

        AlarmList alarmList = new AlarmList();
        alarmList.add(message.getVariableHeader().getAlarm());

        messageSender.sendAlarm(alarmList);

        messageSender.sendHeartbeat(new ArrayList() {{
            add(new Heartbeat(channel.getLineCode(), channel.getSystemCode(), LocalDateTime.now()));
        }});
    }

    @Override
    public void controlResp(WrappedChannel channel, GjkzControlRespMessage message) {
        if (checkLogin) {
            if (channel.getClientId() == null || channel.getClientId().length() == 0) {
                log.info("用户未登录,关闭连接.");
                disConnect(channel, message);
                return;
            }
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'控制响应:{}", channel.id().asShortText(), message);

        messageSender.sendHeartbeat(new ArrayList() {{
            add(new Heartbeat(channel.getLineCode(), channel.getSystemCode(), LocalDateTime.now()));
        }});
    }

    @Override
    public void cresultReq(WrappedChannel channel, GjkzCResultReqMessage message) {
        if (checkLogin) {
            if (channel.getClientId() == null || channel.getClientId().length() == 0) {
                log.info("用户未登录,关闭连接.");
                disConnect(channel, message);
                return;
            }
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'控制结果请求:{}", channel.id().asShortText(), message);

        messageSender.sendHeartbeat(new ArrayList() {{
            add(new Heartbeat(channel.getLineCode(), channel.getSystemCode(), LocalDateTime.now()));
        }});


        GjkzCResultRespMessage gjkzCResultRespMessage = new GjkzCResultRespMessage(new GjkzFixedHeader(GjkzMessageType.CRESULTRESP, 0),
                new GjkzCResultRespVariableHeader(message.getVariableHeader().getMessageId()));
        channel.writeAndFlush(gjkzCResultRespMessage);

        ByteBuf buf = GjkzEncoder.doEncode(gjkzCResultRespMessage);
        log.info("回复客户端'{}'控制结果相应:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
    }

    @Override
    public void readResp(WrappedChannel channel, GjkzReadRespMessage message) {
        if (checkLogin) {
            if (channel.getClientId() == null || channel.getClientId().length() == 0) {
                log.info("用户未登录,关闭连接.");
                disConnect(channel, message);
                return;
            }
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'读响应:{}", channel.id().asShortText(), message);

        messageSender.sendHeartbeat(new ArrayList() {{
            add(new Heartbeat(channel.getLineCode(), channel.getSystemCode(), LocalDateTime.now()));
        }});
    }
}
