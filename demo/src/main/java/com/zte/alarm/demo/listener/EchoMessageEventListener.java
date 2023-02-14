package com.zte.alarm.demo.listener;

import com.zte.alarm.core.codec.gjkz.*;
import com.zte.alarm.core.listener.DefaultGjkzMessageEventListener;
import com.zte.alarm.core.service.north.WrappedChannel;
import com.zte.alarm.core.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 9:45
 */
@Slf4j
public class EchoMessageEventListener extends DefaultGjkzMessageEventListener {

    @Override
    public void connect(WrappedChannel channel, GjkzConnectMessage msg) {
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'连接请求:{}", channel.id().asShortText(), msg);
        int systemCode = 0;
        int lineCode = 0;


        String clientId = msg.getPayload().getClientIdentifier();

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

        channel.setClientId(clientId);


        //发送同步请求命令
        GjkzAlarmSyncReqMessage gjkzAlarmSyncReqMessage = (GjkzAlarmSyncReqMessage) GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.ALARMSYNCREQ, 0),
                null, null);
        channel.writeAndFlush(gjkzAlarmSyncReqMessage);

        ByteBuf buf = GjkzEncoder.doEncode(gjkzAlarmSyncReqMessage);
        log.info("发送客户端'{}'告警同步请求:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
    }

    @Override
    public void pingReq(WrappedChannel channel, GjkzMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.info("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
//        log.info("收到'{}'心跳请求:{}", channel.id().asShortText(), message);

        GjkzMessage pingResp = new GjkzMessage(new GjkzFixedHeader(GjkzMessageType.PINGRESP, 0));
        channel.writeAndFlush(pingResp);

        ByteBuf buf = GjkzEncoder.doEncode(pingResp);
//        log.info("回复客户端'{}'心跳响应:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
    }

    @Override
    public void alarmSyncResp(WrappedChannel channel, GjkzAlarmSyncRespMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.info("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'告警同步响应:{}", channel.id().asShortText(), message);
    }

    @Override
    public void alarmReq(WrappedChannel channel, GjkzAlarmReqMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.info("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'告警请求:{}", channel.id().asShortText(), message);

        GjkzAlarmRespMessage gjkzAlarmRespMessage = new GjkzAlarmRespMessage(
                new GjkzFixedHeader(GjkzMessageType.ALARMRESP, 0),
                new GjkzAlarmRespVariableHeader(message.getVariableHeader().getMessageId()));
        channel.writeAndFlush(gjkzAlarmRespMessage);

        ByteBuf buf = GjkzEncoder.doEncode(gjkzAlarmRespMessage);
        log.info("回复客户端'{}'告警响应:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
    }

    @Override
    public void controlResp(WrappedChannel channel, GjkzControlRespMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.info("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'控制响应:{}", channel.id().asShortText(), message);
    }

    @Override
    public void cresultReq(WrappedChannel channel, GjkzCResultReqMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.info("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'控制结果请求:{}", channel.id().asShortText(), message);

        GjkzCResultRespMessage gjkzCResultRespMessage = new GjkzCResultRespMessage(new GjkzFixedHeader(GjkzMessageType.CRESULTRESP, 0),
                new GjkzCResultRespVariableHeader(message.getVariableHeader().getMessageId()));
        channel.writeAndFlush(gjkzCResultRespMessage);

        ByteBuf buf = GjkzEncoder.doEncode(gjkzCResultRespMessage);
        log.info("回复客户端'{}'控制结果相应:{}", channel.id().asShortText(), CommonUtil.bytesToHexString(buf.array()));
    }

    @Override
    public void readResp(WrappedChannel channel, GjkzReadRespMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.info("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.info("收到'{}'读响应:{}", channel.id().asShortText(), message);
    }
}
