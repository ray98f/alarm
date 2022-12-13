package com.zte.alarm.core.service.north.server;

/**
 * @author wshmang@163.com
 * @date 2021/3/26 11:05
 */
public interface Command {
    /**
     * 刷新告警列表命令
     * @return
     */
    Object getRefreshCommand();
}
