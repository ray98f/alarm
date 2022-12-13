package com.zte.alarm.demo.listener;

import com.zte.alarm.core.listener.EventBehavior;
import com.zte.alarm.core.listener.ExceptionEventListener;
import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 10:33
 */
@Slf4j
public class EchoExceptionEventListener implements ExceptionEventListener {
    @Override
    public EventBehavior exceptionCaught(ChannelHandlerContext ctx, WrappedChannel channel, Throwable cause) {
        log.error("系统异常,断开连接", cause);

        if (channel.isActive()) {
            channel.close();
        }
        return EventBehavior.CONTINUE;
    }
}
