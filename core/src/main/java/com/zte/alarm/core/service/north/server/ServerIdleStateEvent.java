package com.zte.alarm.core.service.north.server;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 15:50
 */
public class ServerIdleStateEvent {


    public static final ServerIdleStateEvent FIRST_READER_IDLE_STATE_EVENT;
    public static final ServerIdleStateEvent READER_IDLE_STATE_EVENT;
    public static final ServerIdleStateEvent FIRST_WRITER_IDLE_STATE_EVENT;
    public static final ServerIdleStateEvent WRITER_IDLE_STATE_EVENT;
    public static final ServerIdleStateEvent FIRST_ALL_IDLE_STATE_EVENT;
    public static final ServerIdleStateEvent ALL_IDLE_STATE_EVENT;
    public static final ServerIdleStateEvent FIRST_HEARTBEAT_IDLE_STATE_EVENT;
    public static final ServerIdleStateEvent HEARTBEAT_IDLE_STATE_EVENT;
    private final ServerIdleState state;
    private final boolean first;

    protected ServerIdleStateEvent(ServerIdleState state, boolean first) {
        this.state = (ServerIdleState) ObjectUtil.checkNotNull(state, "state");
        this.first = first;
    }

    public ServerIdleState state() {
        return this.state;
    }

    public boolean isFirst() {
        return this.first;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + this.state + (this.first ? ", first" : "") + ')';
    }

    static {
        FIRST_READER_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.READER_IDLE, true);
        READER_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.READER_IDLE, false);
        FIRST_WRITER_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.WRITER_IDLE, true);
        WRITER_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.WRITER_IDLE, false);
        FIRST_ALL_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.ALL_IDLE, true);
        ALL_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.ALL_IDLE, false);
        FIRST_HEARTBEAT_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.HEARTBEAT_IDLE, true);
        HEARTBEAT_IDLE_STATE_EVENT = new ServerIdleStateEvent.DefaultIdleStateEvent(ServerIdleState.HEARTBEAT_IDLE, false);
    }

    private static final class DefaultIdleStateEvent extends ServerIdleStateEvent {
        private final String representation;

        DefaultIdleStateEvent(ServerIdleState state, boolean first) {
            super(state, first);
            this.representation = "ServerIdleStateEvent(" + state + (first ? ", first" : "") + ')';
        }

        @Override
        public String toString() {
            return this.representation;
        }
    }
}
