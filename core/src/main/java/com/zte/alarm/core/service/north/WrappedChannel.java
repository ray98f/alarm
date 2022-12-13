package com.zte.alarm.core.service.north;

import com.zte.alarm.core.exception.SocketRuntimeException;
import com.zte.alarm.core.future.InvokeFuture;
import com.zte.alarm.core.pojo.AlarmPackage;
import com.zte.alarm.core.pojo.Request;
import com.zte.alarm.core.pojo.Response;
import com.zte.alarm.core.service.north.server.Server;
import com.zte.alarm.core.service.north.server.ServerContext;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:16
 */

@Slf4j
public class WrappedChannel implements Channel {
    private Channel channel;
    private ConcurrentHashMap<Integer, InvokeFuture> futures = new ConcurrentHashMap<>();
    private long lastHeartbeatTime;
    private int keepAliveTimeSeconds;

    private String clientId;
    private int systemCode;
    private int lineCode;

    private AlarmPackage alarmPackage;

    private final ChannelFutureListener sendSuccessListener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                Server server = ServerContext.getContext().getServer();
                if (server != null) {
//                    server.getCountInfo().getSentNum().incrementAndGet();
//                    server.getCountInfo().setLastSent(System.currentTimeMillis());
                }
            }
        }
    };

    public WrappedChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel can not be null.");
        }
        this.channel = channel;
    }

    public ChannelFuture send(Object message) {
        return this.writeAndFlush(message);
    }

    public Response sendSync(final Request message, int timeout) {
        final InvokeFuture invokeFuture = new InvokeFuture();
        try {
            invokeFuture.setChannel(this);
            // 存储InvokeFuture
            futures.put(message.getSequence(), invokeFuture);

            // 发送Request对象
            ChannelFuture channelFuture = writeAndFlush(message);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        futures.remove(message.getSequence());
                        invokeFuture.setCause(future.cause());
                    }
                }
            });
        } catch (Throwable ex) {
            throw new SocketRuntimeException(ex);
        }

        Object retObj;
        // 设置超时时间
        if (timeout > 0) {
            // 等待返回，直到Response返回或超时
            retObj = invokeFuture.getResult(timeout, TimeUnit.MILLISECONDS);
        } else {
            // 一直等待，直到Response返回
            retObj = invokeFuture.getResult();
        }

        return (Response) retObj;
    }

    public ConcurrentHashMap<Integer, InvokeFuture> getFutures() {
        return futures;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> arg0) {
        return channel.attr(arg0);
    }

    @Override
    public int compareTo(Channel o) {
        return channel.compareTo(o);
    }

    @Override
    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    @Override
    public ChannelFuture bind(SocketAddress arg0) {
        return channel.bind(arg0);
    }

    @Override
    public ChannelFuture bind(SocketAddress arg0, ChannelPromise arg1) {
        return channel.bind(arg0, arg1);
    }

    @Override
    public ChannelFuture close() {
        return this.innerClose();
    }

    @Override
    public ChannelFuture close(ChannelPromise arg0) {
        return channel.close(arg0);
    }

    @Override
    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    @Override
    public ChannelConfig config() {
        return channel.config();
    }

    @Override
    public ChannelFuture connect(SocketAddress arg0) {
        return channel.connect(arg0);
    }

    @Override
    public ChannelFuture connect(SocketAddress arg0, SocketAddress arg1) {
        return channel.connect(arg0, arg1);
    }

    @Override
    public ChannelFuture connect(SocketAddress arg0, ChannelPromise arg1) {
        return channel.connect(arg0, arg1);
    }

    @Override
    public ChannelFuture connect(SocketAddress arg0, SocketAddress arg1, ChannelPromise arg2) {
        return channel.connect(arg0, arg1, arg2);
    }

    @Override
    public ChannelFuture deregister() {
        return channel.deregister();
    }

    @Override
    public ChannelFuture deregister(ChannelPromise arg0) {
        return channel.deregister(arg0);
    }

    @Override
    public ChannelFuture disconnect() {
        return channel.disconnect();
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise arg0) {
        return channel.deregister(arg0);
    }

    @Override
    public EventLoop eventLoop() {
        return channel.eventLoop();
    }

    @Override
    public Channel flush() {
        return channel.flush();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return channel.isRegistered();
    }

    @Override
    public boolean isWritable() {
        return channel.isWritable();
    }

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return channel.metadata();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable arg0) {
        return channel.newFailedFuture(arg0);
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return channel.newProgressivePromise();
    }

    @Override
    public ChannelPromise newPromise() {
        return channel.newPromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return channel.newSucceededFuture();
    }

    @Override
    public Channel parent() {
        return channel.parent();
    }

    @Override
    public ChannelPipeline pipeline() {
        return channel.pipeline();
    }

    @Override
    public Channel read() {
        return channel.read();
    }

    @Override
    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public Unsafe unsafe() {
        return channel.unsafe();
    }

    @Override
    public ChannelPromise voidPromise() {
        return channel.voidPromise();
    }

    @Override
    public ChannelFuture write(Object message) {
        return this.write(message, true);
    }

    public ChannelFuture write(Object message, boolean isStatistic) {
        ChannelFuture future = channel.write(message);
        if (isStatistic) {
            future.addListener(sendSuccessListener);
        }
        return future;
    }

    @Override
    public ChannelFuture write(Object message, ChannelPromise channelPromise) {
        return this.write(message, channelPromise, true);
    }

    public ChannelFuture write(Object message, ChannelPromise channelPromise, boolean isStatistic) {
        ChannelFuture future = channel.write(message, channelPromise);
        if (isStatistic) {
            future.addListener(sendSuccessListener);
        }
        return future;
    }

    @Override
    public ChannelFuture writeAndFlush(Object message) {
        return this.writeAndFlush(message, true);
    }

    public ChannelFuture writeAndFlush(Object message, boolean isStatistic) {
        ChannelFuture future = channel.writeAndFlush(message);
        if (isStatistic) {
            future.addListener(sendSuccessListener);
        }
        return future;
    }

    @Override
    public ChannelFuture writeAndFlush(Object message, ChannelPromise channelPromise) {
        return this.writeAndFlush(message, channelPromise, true);
    }

    public ChannelFuture writeAndFlush(Object message, ChannelPromise channelPromise, boolean isStatistic) {
        ChannelFuture future = channel.writeAndFlush(message, channelPromise);
        if (isStatistic) {
            future.addListener(sendSuccessListener);
        }
        return future;
    }

    private ChannelFuture innerClose() {
        ChannelFuture channelFuture = channel.close();

        // cancel所有等待中的InvokeFuture
        for (InvokeFuture future : futures.values()) {
            if (!future.isDone()) {
                future.setCause(new ClosedChannelException());
            }
        }
        return channelFuture;
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> arg0) {
        return channel.hasAttr(arg0);
    }

    @Override
    public long bytesBeforeUnwritable() {
        return channel.bytesBeforeUnwritable();
    }

    @Override
    public long bytesBeforeWritable() {
        return channel.bytesBeforeUnwritable();
    }

    @Override
    public ChannelId id() {
        return channel.id();
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public int getKeepAliveTimeSeconds() {
        return keepAliveTimeSeconds;
    }

    public void setKeepAliveTimeSeconds(int keepAliveTimeSeconds) {
        this.keepAliveTimeSeconds = keepAliveTimeSeconds;
    }

    public void setClientId(String clientId, int systemCode, int lineCode) {
        this.clientId = clientId;
        this.systemCode = systemCode;
        this.lineCode = lineCode;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return this.clientId;
    }

    public AlarmPackage getAlarmPackage() {
        return this.alarmPackage;
    }

    public void setAlarmPackage(AlarmPackage alarmPackage) {
        this.alarmPackage = alarmPackage;
    }

    public int getSystemCode() {
        return systemCode;
    }

    public int getLineCode() {
        return lineCode;
    }
}
