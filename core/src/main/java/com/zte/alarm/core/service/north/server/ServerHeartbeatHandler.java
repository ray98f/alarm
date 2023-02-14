package com.zte.alarm.core.service.north.server;

import com.zte.alarm.core.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 14:29
 */
@Slf4j
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerHeartbeatHandler.class);

    public ServerHeartbeatHandler() {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ServerIdleStateEvent) {
            ServerIdleStateEvent e = (ServerIdleStateEvent) evt;
            if (e.state() == ServerIdleState.WRITER_IDLE) {
            } else if (e.state() == ServerIdleState.READER_IDLE) {
                ctx.channel().close();
            } else if (e.state() == ServerIdleState.HEARTBEAT_IDLE) {
//                log.warn("长时间未收到客户端心跳数据,断开连接.");
                ctx.channel().close();
            } else {
//                log.warn("长时间未收读写数据,断开连接.");
                ctx.channel().close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] barray = new byte[buf.readableBytes()];
        buf.getBytes(0, barray);
        String str = CommonUtil.bytesToHexString(barray);
        if (!str.startsWith("03 00")) {
            logger.info("收到数据:{}", CommonUtil.bytesToHexString(barray));
        }
        super.channelRead(ctx, msg);
    }
}
