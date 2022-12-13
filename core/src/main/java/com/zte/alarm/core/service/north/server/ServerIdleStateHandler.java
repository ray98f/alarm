package com.zte.alarm.core.service.north.server;

import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.channel.*;
import io.netty.util.internal.ObjectUtil;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 15:04
 */
public class ServerIdleStateHandler extends ChannelDuplexHandler {
    private static final long MIN_TIMEOUT_NANOS;
    private final ChannelFutureListener writeListener;
    private final boolean observeOutput;
    private final long readerIdleTimeNanos;
    private final long writerIdleTimeNanos;
    private final long allIdleTimeNanos;
    private final long heartbeatIdleTimeNanos;
    private ScheduledFuture<?> readerIdleTimeout;
    private long lastReadTime;
    private boolean firstReaderIdleEvent;
    private ScheduledFuture<?> writerIdleTimeout;
    private long lastWriteTime;
    private boolean firstWriterIdleEvent;
    private ScheduledFuture<?> allIdleTimeout;
    private boolean firstAllIdleEvent;
    private ScheduledFuture<?> heartbeatIdleTimeout;
    private boolean firstHeartbeatIdleEvent;
    private byte state;
    private boolean reading;
    private long lastChangeCheckTimeStamp;
    private int lastMessageHashCode;
    private long lastPendingWriteBytes;
    private long lastFlushProgress;

    private Map<String, WrappedChannel> channels;

    public void setChannels(Map<String, WrappedChannel> channels) {
        this.channels = channels;
    }

    public ServerIdleStateHandler(Map<String, WrappedChannel> channels, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds, int heartbeatIdleTimeSeconds) {
        this(channels, (long) readerIdleTimeSeconds, (long) writerIdleTimeSeconds, (long) allIdleTimeSeconds, (long) heartbeatIdleTimeSeconds, TimeUnit.SECONDS);
    }

    public ServerIdleStateHandler(Map<String, WrappedChannel> channels, long readerIdleTime, long writerIdleTime, long allIdleTime, long heartbeatIdleTime, TimeUnit unit) {
        this(channels, false, readerIdleTime, writerIdleTime, allIdleTime, heartbeatIdleTime, unit);
    }

    public ServerIdleStateHandler(Map<String, WrappedChannel> channels, boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, long heartbeatIdleTime, TimeUnit unit) {
        this.writeListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ServerIdleStateHandler.this.lastWriteTime = ServerIdleStateHandler.this.ticksInNanos();
                ServerIdleStateHandler.this.firstWriterIdleEvent = ServerIdleStateHandler.this.firstAllIdleEvent = true;
            }
        };
        this.channels = channels;
        this.firstReaderIdleEvent = true;
        this.firstWriterIdleEvent = true;
        this.firstAllIdleEvent = true;
        this.firstHeartbeatIdleEvent = true;
        ObjectUtil.checkNotNull(unit, "unit");
        this.observeOutput = observeOutput;
        if (readerIdleTime <= 0L) {
            this.readerIdleTimeNanos = 0L;
        } else {
            this.readerIdleTimeNanos = Math.max(unit.toNanos(readerIdleTime), MIN_TIMEOUT_NANOS);
        }

        if (writerIdleTime <= 0L) {
            this.writerIdleTimeNanos = 0L;
        } else {
            this.writerIdleTimeNanos = Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS);
        }

        if (allIdleTime <= 0L) {
            this.allIdleTimeNanos = 0L;
        } else {
            this.allIdleTimeNanos = Math.max(unit.toNanos(allIdleTime), MIN_TIMEOUT_NANOS);
        }

        if (heartbeatIdleTime <= 0L) {
            this.heartbeatIdleTimeNanos = 0L;
        } else {
            this.heartbeatIdleTimeNanos = Math.max(unit.toNanos(heartbeatIdleTime), MIN_TIMEOUT_NANOS);
        }
    }

    public long getReaderIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.readerIdleTimeNanos);
    }

    public long getWriterIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.writerIdleTimeNanos);
    }

    public long getAllIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.allIdleTimeNanos);
    }

    public long getHeartbeatIdleTimeInMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.heartbeatIdleTimeNanos);
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive() && ctx.channel().isRegistered()) {
            this.initialize(ctx);
        }

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            this.initialize(ctx);
        }

        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.initialize(ctx);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.destroy();
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
            this.reading = true;
            this.firstReaderIdleEvent = this.firstAllIdleEvent = true;
        }

        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if ((this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) && this.reading) {
            this.lastReadTime = this.ticksInNanos();
            this.reading = false;
        }

        ctx.fireChannelReadComplete();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.writerIdleTimeNanos <= 0L && this.allIdleTimeNanos <= 0L) {
            ctx.write(msg, promise);
        } else {
            ctx.write(msg, promise.unvoid()).addListener(this.writeListener);
        }

    }

    private void initialize(ChannelHandlerContext ctx) {
        switch (this.state) {
            case 1:
            case 2:
                return;
            default:
                this.state = 1;
                this.initOutputChanged(ctx);
                this.lastReadTime = this.lastWriteTime = this.ticksInNanos();
                if (this.readerIdleTimeNanos > 0L) {
                    this.readerIdleTimeout = this.schedule(ctx, new ServerIdleStateHandler.ReaderIdleTimeoutTask(ctx), this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
                }

                if (this.writerIdleTimeNanos > 0L) {
                    this.writerIdleTimeout = this.schedule(ctx, new ServerIdleStateHandler.WriterIdleTimeoutTask(ctx), this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
                }

                if (this.allIdleTimeNanos > 0L) {
                    this.allIdleTimeout = this.schedule(ctx, new ServerIdleStateHandler.AllIdleTimeoutTask(ctx), this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
                }

                if (this.heartbeatIdleTimeNanos > 0L) {
                    this.heartbeatIdleTimeout = this.schedule(ctx, new ServerIdleStateHandler.HeartbeatIdleTimeoutTask(ctx), this.heartbeatIdleTimeNanos, TimeUnit.NANOSECONDS);
                }
        }
    }

    long ticksInNanos() {
        return System.nanoTime();
    }

    ScheduledFuture<?> schedule(ChannelHandlerContext ctx, Runnable task, long delay, TimeUnit unit) {
        return ctx.executor().schedule(task, delay, unit);
    }

    private void destroy() {
        this.state = 2;
        if (this.readerIdleTimeout != null) {
            this.readerIdleTimeout.cancel(false);
            this.readerIdleTimeout = null;
        }

        if (this.writerIdleTimeout != null) {
            this.writerIdleTimeout.cancel(false);
            this.writerIdleTimeout = null;
        }

        if (this.allIdleTimeout != null) {
            this.allIdleTimeout.cancel(false);
            this.allIdleTimeout = null;
        }

        if (this.heartbeatIdleTimeout != null) {
            this.heartbeatIdleTimeout.cancel(false);
            this.heartbeatIdleTimeout = null;
        }

    }

    protected void channelIdle(ChannelHandlerContext ctx, ServerIdleStateEvent evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }

    protected ServerIdleStateEvent newIdleStateEvent(ServerIdleState state, boolean first) {
        switch (state) {
            case ALL_IDLE:
                return first ? ServerIdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT : ServerIdleStateEvent.ALL_IDLE_STATE_EVENT;
            case READER_IDLE:
                return first ? ServerIdleStateEvent.FIRST_READER_IDLE_STATE_EVENT : ServerIdleStateEvent.READER_IDLE_STATE_EVENT;
            case WRITER_IDLE:
                return first ? ServerIdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT : ServerIdleStateEvent.WRITER_IDLE_STATE_EVENT;
            case HEARTBEAT_IDLE:
                return first ? ServerIdleStateEvent.FIRST_HEARTBEAT_IDLE_STATE_EVENT : ServerIdleStateEvent.HEARTBEAT_IDLE_STATE_EVENT;
            default:
                throw new IllegalArgumentException("Unhandled: state=" + state + ", first=" + first);
        }
    }

    private void initOutputChanged(ChannelHandlerContext ctx) {
        if (this.observeOutput) {
            Channel channel = ctx.channel();
            Channel.Unsafe unsafe = channel.unsafe();
            ChannelOutboundBuffer buf = unsafe.outboundBuffer();
            if (buf != null) {
                this.lastMessageHashCode = System.identityHashCode(buf.current());
                this.lastPendingWriteBytes = buf.totalPendingWriteBytes();
                this.lastFlushProgress = buf.currentProgress();
            }
        }

    }

    private boolean hasOutputChanged(ChannelHandlerContext ctx, boolean first) {
        if (this.observeOutput) {
            if (this.lastChangeCheckTimeStamp != this.lastWriteTime) {
                this.lastChangeCheckTimeStamp = this.lastWriteTime;
                if (!first) {
                    return true;
                }
            }

            Channel channel = ctx.channel();
            Channel.Unsafe unsafe = channel.unsafe();
            ChannelOutboundBuffer buf = unsafe.outboundBuffer();
            if (buf != null) {
                int messageHashCode = System.identityHashCode(buf.current());
                long pendingWriteBytes = buf.totalPendingWriteBytes();
                if (messageHashCode != this.lastMessageHashCode || pendingWriteBytes != this.lastPendingWriteBytes) {
                    this.lastMessageHashCode = messageHashCode;
                    this.lastPendingWriteBytes = pendingWriteBytes;
                    if (!first) {
                        return true;
                    }
                }

                long flushProgress = buf.currentProgress();
                if (flushProgress != this.lastFlushProgress) {
                    this.lastFlushProgress = flushProgress;
                    if (!first) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    static {
        MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
    }

    private final class HeartbeatIdleTimeoutTask extends ServerIdleStateHandler.AbstractIdleTask {
        HeartbeatIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void run(ChannelHandlerContext ctx) {

            WrappedChannel wrappedChannel = channels.get(ctx.channel().id().asShortText());

            long nextDelay = ServerIdleStateHandler.this.heartbeatIdleTimeNanos;
            if (!ServerIdleStateHandler.this.reading) {
                nextDelay -= ServerIdleStateHandler.this.ticksInNanos() - wrappedChannel.getLastHeartbeatTime();
            }

            if (nextDelay <= 0L) {
                ServerIdleStateHandler.this.heartbeatIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, ServerIdleStateHandler.this.heartbeatIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = ServerIdleStateHandler.this.firstHeartbeatIdleEvent;
                ServerIdleStateHandler.this.firstHeartbeatIdleEvent = false;

                try {
                    if (ServerIdleStateHandler.this.hasOutputChanged(ctx, first)) {
                        return;
                    }

                    ServerIdleStateEvent event = ServerIdleStateHandler.this.newIdleStateEvent(ServerIdleState.HEARTBEAT_IDLE, first);
                    ServerIdleStateHandler.this.channelIdle(ctx, event);
                } catch (Throwable var6) {
                    ctx.fireExceptionCaught(var6);
                }
            } else {
                ServerIdleStateHandler.this.allIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }

        }
    }

    private final class AllIdleTimeoutTask extends ServerIdleStateHandler.AbstractIdleTask {
        AllIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void run(ChannelHandlerContext ctx) {
            long nextDelay = ServerIdleStateHandler.this.allIdleTimeNanos;
            if (!ServerIdleStateHandler.this.reading) {
                nextDelay -= ServerIdleStateHandler.this.ticksInNanos() - Math.max(ServerIdleStateHandler.this.lastReadTime, ServerIdleStateHandler.this.lastWriteTime);
            }

            if (nextDelay <= 0L) {
                ServerIdleStateHandler.this.allIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, ServerIdleStateHandler.this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = ServerIdleStateHandler.this.firstAllIdleEvent;
                ServerIdleStateHandler.this.firstAllIdleEvent = false;

                try {
                    if (ServerIdleStateHandler.this.hasOutputChanged(ctx, first)) {
                        return;
                    }

                    ServerIdleStateEvent event = ServerIdleStateHandler.this.newIdleStateEvent(ServerIdleState.ALL_IDLE, first);
                    ServerIdleStateHandler.this.channelIdle(ctx, event);
                } catch (Throwable var6) {
                    ctx.fireExceptionCaught(var6);
                }
            } else {
                ServerIdleStateHandler.this.allIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }

        }
    }

    private final class WriterIdleTimeoutTask extends ServerIdleStateHandler.AbstractIdleTask {
        WriterIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void run(ChannelHandlerContext ctx) {
            long lastWriteTime = ServerIdleStateHandler.this.lastWriteTime;
            long nextDelay = ServerIdleStateHandler.this.writerIdleTimeNanos - (ServerIdleStateHandler.this.ticksInNanos() - lastWriteTime);
            if (nextDelay <= 0L) {
                ServerIdleStateHandler.this.writerIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, ServerIdleStateHandler.this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = ServerIdleStateHandler.this.firstWriterIdleEvent;
                ServerIdleStateHandler.this.firstWriterIdleEvent = false;

                try {
                    if (ServerIdleStateHandler.this.hasOutputChanged(ctx, first)) {
                        return;
                    }

                    ServerIdleStateEvent event = ServerIdleStateHandler.this.newIdleStateEvent(ServerIdleState.WRITER_IDLE, first);
                    ServerIdleStateHandler.this.channelIdle(ctx, event);
                } catch (Throwable var8) {
                    ctx.fireExceptionCaught(var8);
                }
            } else {
                ServerIdleStateHandler.this.writerIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }

        }
    }

    private final class ReaderIdleTimeoutTask extends ServerIdleStateHandler.AbstractIdleTask {
        ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        protected void run(ChannelHandlerContext ctx) {
            long nextDelay = ServerIdleStateHandler.this.readerIdleTimeNanos;
            if (!ServerIdleStateHandler.this.reading) {
                nextDelay -= ServerIdleStateHandler.this.ticksInNanos() - ServerIdleStateHandler.this.lastReadTime;
            }

            if (nextDelay <= 0L) {
                ServerIdleStateHandler.this.readerIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, ServerIdleStateHandler.this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
                boolean first = ServerIdleStateHandler.this.firstReaderIdleEvent;
                ServerIdleStateHandler.this.firstReaderIdleEvent = false;

                try {
                    ServerIdleStateEvent event = ServerIdleStateHandler.this.newIdleStateEvent(ServerIdleState.READER_IDLE, first);
                    ServerIdleStateHandler.this.channelIdle(ctx, event);
                } catch (Throwable var6) {
                    ctx.fireExceptionCaught(var6);
                }
            } else {
                ServerIdleStateHandler.this.readerIdleTimeout = ServerIdleStateHandler.this.schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
            }

        }
    }

    private abstract static class AbstractIdleTask implements Runnable {
        private final ChannelHandlerContext ctx;

        AbstractIdleTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (this.ctx.channel().isOpen()) {
                this.run(this.ctx);
            }
        }

        protected abstract void run(ChannelHandlerContext var1);
    }
}
