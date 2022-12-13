package com.zte.alarm.core.service.north.server;

/**
 * @author wshmang@163.com
 * @date 2021/3/18 15:47
 */
public enum ServerIdleState {
    READER_IDLE,
    WRITER_IDLE,
    ALL_IDLE,
    HEARTBEAT_IDLE;

    private ServerIdleState() {
    }
}
