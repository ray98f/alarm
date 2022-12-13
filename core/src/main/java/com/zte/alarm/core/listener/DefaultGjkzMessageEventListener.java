package com.zte.alarm.core.listener;

import com.zte.alarm.core.codec.gjkz.*;
import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:14
 */
@Slf4j
public class DefaultGjkzMessageEventListener implements MessageEventListener {
    @Override
    public EventBehavior channelRead(ChannelHandlerContext ctx, WrappedChannel channel, Object msg) {
        if (msg == null) {
            log.error("数据解析异常");
            return EventBehavior.CONTINUE;
        }
        if (msg instanceof GjkzMessage) {
            GjkzMessage message = (GjkzMessage) msg;
            if (message.getDecoderResult().cause() != null) {
                log.error("数据解析异常", message.getDecoderResult().cause());
                return EventBehavior.CONTINUE;
            }
            GjkzMessageType messageType = message.getGjkzFixedHeader().messageType();
            switch (messageType) {
                case CONNECT:
                    this.connect(channel, (GjkzConnectMessage) message);
                    break;
                case CONNACK:
                    this.connAck(channel, message);
                    break;
                case PINGREQ:
                    this.pingReq(channel, message);
                    break;
                case PINGRESP:
                    this.pingResp(channel, message);
                    break;
                case DISCONNECT:
                    this.disConnect(channel, message);
                    break;
                case ALARMSYNCREQ:
                    this.alarmSyncReq(channel, (GjkzAlarmSyncReqMessage) message);
                    break;
                case ALARMSYNCRESP:
                    this.alarmSyncResp(channel, (GjkzAlarmSyncRespMessage) message);
                    break;
                case ALARMREQ:
                    this.alarmReq(channel, (GjkzAlarmReqMessage) message);
                    break;
                case ALARMRESP:
                    this.alarmResp(channel, (GjkzAlarmRespMessage) message);
                    break;
                case CONTROLREQ:
                    this.controlReq(channel, (GjkzControlReqMessage) message);
                    break;
                case CONTROLRESP:
                    this.controlResp(channel, (GjkzControlRespMessage) message);
                    break;
                case CRESULTREQ:
                    this.cresultReq(channel, (GjkzCResultReqMessage) message);
                    break;
                case CRESULTRESP:
                    this.cResultResp(channel, (GjkzCResultRespMessage) message);
                    break;
                case READREQ:
                    this.readReq(channel, (GjkzReadReqMessage) message);
                    break;
                case READRESP:
                    this.readResp(channel, (GjkzReadRespMessage) message);
                    break;
                default:
                    log.error("不支持的消息类型：  '{}'.", messageType);
                    break;
            }
        }
        return EventBehavior.CONTINUE;
    }

    public void connect(WrappedChannel channel, GjkzConnectMessage message) {
        log.debug("收到连接请求:{}", message);
    }

    public void connAck(WrappedChannel channel, GjkzMessage message) {
        log.debug("收到连接响应数据:{}", message);
    }

    public void pingReq(WrappedChannel channel, GjkzMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.debug("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.debug("收到心跳数据:{}", message);
    }

    public void pingResp(WrappedChannel channel, GjkzMessage message) {
        log.debug("收到心跳响应数据:{}", message);
    }

    public void disConnect(WrappedChannel channel, GjkzMessage message) {
        if (channel.isActive()) {
            channel.close();

            if (log.isDebugEnabled()) {
                log.debug("客户端 '{}' 关闭连接.", channel.id().asShortText());
            }
        }
    }

    public void alarmSyncReq(WrappedChannel channel, GjkzAlarmSyncReqMessage message) {
        log.debug("收到告警同步请求数据:{}", message);
    }

    public void alarmSyncResp(WrappedChannel channel, GjkzAlarmSyncRespMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.debug("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.debug("收到告警同步数据:{}", message);

    }

    public void alarmReq(WrappedChannel channel, GjkzAlarmReqMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.debug("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        log.debug("收到告警信息:{}", message);

        //TODO 收到告警信息处理

    }

    public void alarmResp(WrappedChannel channel, GjkzAlarmRespMessage message) {
        log.debug("收到告警响应数据:{}", message);
    }

    public void controlReq(WrappedChannel channel, GjkzControlReqMessage message) {
        log.debug("收到控制请求数据:{}", message);
    }

    public void controlResp(WrappedChannel channel, GjkzControlRespMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.debug("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        //TODO  控制命令响应
        log.debug("收到控制响应数据:{}", message);
    }

    public void cresultReq(WrappedChannel channel, GjkzCResultReqMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.debug("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        //TODO  控制结果
        log.debug("收到控制结果数据:{}", message);
    }

    public void cResultResp(WrappedChannel channel, GjkzCResultRespMessage message) {
        log.debug("收到控制结果响应数据:{}", message);
    }

    public void readReq(WrappedChannel channel, GjkzReadReqMessage message) {
        log.debug("收到读请求数据:{}", message);
    }

    public void readResp(WrappedChannel channel, GjkzReadRespMessage message) {
        if (channel.getClientId() == null || channel.getClientId().length() == 0) {
            log.debug("用户未登录,关闭连接.");
            disConnect(channel, message);
            return;
        }
        channel.setLastHeartbeatTime(System.nanoTime());
        //TODO  读命令响应
        log.debug("收到读命令数据:{}", message);
    }

}
