package com.zte.alarm.core.listener;

import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;

import java.util.EventListener;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:14
 */
public interface MessageEventListener extends EventListener {
    /**
     * 接收消息
     *
     * @param ctx
     * @param channel
     * @param msg
     * @return
     */
    EventBehavior channelRead(ChannelHandlerContext ctx, WrappedChannel channel, Object msg);
}
