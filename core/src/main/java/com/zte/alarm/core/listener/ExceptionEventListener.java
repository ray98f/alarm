package com.zte.alarm.core.listener;

import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;

import java.util.EventListener;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:38
 */
public interface ExceptionEventListener extends EventListener {
    /**
     * 异常捕获
     *
     * @param ctx
     * @param channel
     * @param cause
     * @return
     */
    EventBehavior exceptionCaught(ChannelHandlerContext ctx, WrappedChannel channel, Throwable cause);
}