package com.zte.alarm.core.service.north;

import com.zte.alarm.core.service.north.server.Command;
import com.zte.alarm.core.service.north.server.ServerIdleStateHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:51
 */

@Data
public abstract class Service {

    /**
     * socket类型
     */
    protected SocketType socketType = SocketType.GJKZ;

    /**
     * 绑定端口，默认为8000
     */
    protected int port = 8000;

    /**
     * 是否开户业务处理线程池
     */
    protected boolean openExecutor = false;

    /**
     * 工作线程池大小
     */
    protected int workerCount;

    /**
     * 消息事件业务处理线程池
     */
    protected ExecutorService messageExecutor;

    /**
     * 异常事件业务处理线程池
     */
    protected ExecutorService exceptionExecutor;

    /**
     * 通道事件业务处理线程池
     */
    protected ExecutorService channelExecutor;

    /**
     * 消息事件业务处理线程池最小保持的线程数
     */
    protected int corePoolSize = 10;

    /**
     * 消息事件业务处理线程池最大线程数
     */
    protected int maximumPoolSize = 150;

    /**
     * 消息事件业务处理线程池队列最大值
     */
    protected int queueCapacity = 1000000;

    /**
     * 是否启用keepAlive
     */
    protected boolean keepAlive = true;
    /**
     * 是否启用tcpNoDelay
     */
    protected boolean tcpNoDelay = true;

    /**
     * 设置是否心跳检查
     */
    protected boolean checkHeartbeat = true;
    /**
     * 心跳检查时的读空闲时间
     */
    protected int readerIdleTimeSeconds = 0;
    /**
     * 心跳检查时的写空闲时间
     */
    protected int writerIdleTimeSeconds = 0;
    /**
     * 心跳检查时的读写空闲时间
     */
    protected int allIdleTimeSeconds = 90;
    /**
     * 自定义心跳检查空闲时间
     */
    protected int heartbeatIdleTimeSeconds = 11;

    protected ServerIdleStateHandler timeoutHandler;

    protected ChannelInboundHandlerAdapter heartbeatHandler;


    protected LinkedHashMap<String, ChannelHandler> handlers = new LinkedHashMap<>();

    protected List<EventListener> eventListeners = new ArrayList<>();

    protected EventDispatcher eventDispatcher;

    protected Command command;

    public Service() {
        // 默认工作线程数
        this.workerCount = Runtime.getRuntime().availableProcessors() + 1;
        //添加JVM关闭时的勾子
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }


    protected void init() {
        if (openExecutor) {
            messageExecutor = new ThreadPoolExecutor(
                    this.corePoolSize,
                    this.maximumPoolSize,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(this.queueCapacity),
                    new BasicThreadFactory.Builder().namingPattern("MessageEventProcessor-%d").daemon(true).build(),
                    new ThreadPoolExecutor.AbortPolicy());

            exceptionExecutor = Executors.newCachedThreadPool(
                    new BasicThreadFactory.Builder().namingPattern("ExceptionEventProcessor-%d").daemon(true).build());

            channelExecutor = Executors.newCachedThreadPool(
                    new BasicThreadFactory.Builder().namingPattern("ChannelEventProcessor-%d").daemon(true).build());
        }
    }

    public LinkedHashMap<String, ChannelHandler> getHandlers() {
        return handlers;
    }

    public void addEventListener(EventListener listener) {
        this.eventListeners.add(listener);
    }

    public void setListeners(List<EventListener> listeners) {
        if (listeners == null) {
            listeners = new ArrayList<EventListener>();
        }
        eventListeners = listeners;
    }


    public List<EventListener> getEventListeners() {
        return eventListeners;
    }

    class ShutdownHook extends Thread {
        private Service service;

        public ShutdownHook(Service service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.shutdown();
        }
    }

    /**
     * shutdown
     */
    public abstract void shutdown();

    public void setCommand(Command command){
        this.command = command;
    }

}
