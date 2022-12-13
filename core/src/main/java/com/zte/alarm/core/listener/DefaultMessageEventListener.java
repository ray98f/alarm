package com.zte.alarm.core.listener;

import com.zte.alarm.core.future.InvokeFuture;
import com.zte.alarm.core.pojo.Request;
import com.zte.alarm.core.pojo.Response;
import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:45
 */
@Slf4j
public class DefaultMessageEventListener implements MessageEventListener {

    @Override
    public EventBehavior channelRead(ChannelHandlerContext ctx, WrappedChannel channel, Object msg) {
        if (log.isDebugEnabled()) {
            log.debug("收到消息,channel:'{}'.", channel.id().asShortText());
        }

        if (msg != null) {
            if (msg instanceof Request) {
                Request request = (Request) msg;
                if (request.getMessage() != null) {
                    processRequest(ctx, channel, request);
                }
            } else if (msg instanceof Response) {
                Response response = (Response) msg;
                processResponse(ctx, response, channel);
            }
        }
        return EventBehavior.CONTINUE;
    }

    private void processRequest(ChannelHandlerContext ctx, WrappedChannel channel, Request request) {
    }

    private void processResponse(ChannelHandlerContext ctx, Response response, WrappedChannel channel) {
        if (log.isDebugEnabled()) {
            log.debug("回复消息:'{}'.", response);
        }
        InvokeFuture future = channel.getFutures().remove(response.getSequence());
        if (future != null) {
            if (response.getCause() != null) {
                future.setCause(response.getCause());
            } else {
                future.setResult(response);
            }
        }
    }
}
