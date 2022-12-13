package com.zte.alarm.core.listener;

import java.util.EventListener;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 13:39
 */
public interface SnmpMessageEventListener extends EventListener {


    /**
     * snmp 消息
     * @param msg
     * @return
     */
    EventBehavior message(Object msg) throws Exception;
}
