package com.zte.alarm.core.listener;

import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;

import java.util.EventListener;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:38
 */
public interface ChannelEventListener extends EventListener {

    /**
     * 通道连接
     *
     * @param ctx
     * @param channel
     * @return
     */
    EventBehavior channelActive(ChannelHandlerContext ctx, WrappedChannel channel);

    /**
     * 通道关闭
     *
     * @param ctx
     * @param channel
     * @return
     */
    EventBehavior channelInactive(ChannelHandlerContext ctx, WrappedChannel channel);
}
