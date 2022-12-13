package com.zte.alarm.core.service.north.server;

import com.zte.alarm.core.codec.gjkz.GjkzDecoder;
import com.zte.alarm.core.codec.gjkz.GjkzEncoder;
import com.zte.alarm.core.service.north.EventDispatcher;
import com.zte.alarm.core.service.north.Service;
import com.zte.alarm.core.service.north.SocketType;
import com.zte.alarm.core.service.north.WrappedChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:49
 */
@Slf4j
public class Server extends Service {

    protected ConcurrentHashMap<String, WrappedChannel> channels = new ConcurrentHashMap<>();

    protected EventLoopGroup bossGroup;

    protected EventLoopGroup workerGroup;

    protected ServerBootstrap bootstrap;

    @Override
    protected void init() {
        super.init();
        if (useEpoll()) {
            bossGroup = new EpollEventLoopGroup(workerCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "LINUX_BOSS_" + index.incrementAndGet());
                }
            });
            workerGroup = new EpollEventLoopGroup(workerCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "LINUX_WORK_" + index.incrementAndGet());
                }
            });
        } else {
            bossGroup = new NioEventLoopGroup(workerCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "BOSS_" + index.incrementAndGet());
                }
            });
            workerGroup = new NioEventLoopGroup(workerCount, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "WORK_" + index.incrementAndGet());
                }
            });
        }


        eventDispatcher = new EventDispatcher(this);


//        this.addEventListener(new DefaultMessageEventListener());

    }

    @Override
    public void shutdown() {

    }


    public ChannelFuture bind() {
        init();

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, keepAlive);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, tcpNoDelay);
        bootstrap.channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                LinkedHashMap<String, ChannelHandler> handlers = getHandlers();
                for (String key : handlers.keySet()) {
                    pipeline.addLast(key, handlers.get(key));
                }

                if (checkHeartbeat) {
                    pipeline.addLast("timeoutHandler", new ServerIdleStateHandler(getChannels(), readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, heartbeatIdleTimeSeconds));
                    pipeline.addLast("heartbeatHandler", new ServerHeartbeatHandler());
                }

                if (socketType.equals(SocketType.GJKZ)) {
                    pipeline.addLast("gjkzDecoder", new GjkzDecoder());
                    pipeline.addLast("gjkzEncoder", new GjkzEncoder());
                }

                ServerDispatchHandler dispatchHandler = new ServerDispatchHandler(eventDispatcher);
                pipeline.addLast("dispatchHandler", dispatchHandler);
            }
        });

        final InetSocketAddress socketAddress = new InetSocketAddress(port);
        ChannelFuture future = bootstrap.bind(socketAddress);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture ch) throws Exception {
                ch.await();
                if (ch.isSuccess()) {
                    log.info("服务已启动,正在监听中... {}.", socketAddress);
                } else {
                    log.error("服务启动失败'{}'.", socketAddress, ch.cause());
                }
            }
        });

        return future;
    }

    public Map<String, WrappedChannel> getChannels() {
        return channels;
    }

    public WrappedChannel getChannel(String channelId) {
        if (channelId != null) {
            return channels.get(channelId);
        } else {
            return null;
        }
    }

    public boolean useEpoll() {
        String osName = System.getProperty("os.name");
        boolean isLinuxPlatform = StringUtils.containsIgnoreCase(osName, "linux");
        return isLinuxPlatform && Epoll.isAvailable();
    }

    public void refresh() {
        Object refreshCommand = command.getRefreshCommand();
        for (Map.Entry<String, WrappedChannel> entry : getChannels().entrySet()) {
            entry.getValue().send(refreshCommand);
        }
    }
}
