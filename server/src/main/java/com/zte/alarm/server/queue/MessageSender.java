package com.zte.alarm.server.queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wshmang@163.com
 * @date 2021/3/22 17:20
 */
@Component
public class MessageSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendHeartbeat(Object msg) {
        rabbitTemplate.convertAndSend("HEARTBEAT_QUEUE", msg);
    }

    public void sendAlarm(Object msg) {
        rabbitTemplate.convertAndSend("ALARM_QUEUE", msg);
    }

    public void sendSyncAlarm(Object msg){
        rabbitTemplate.convertAndSend("SYNC_ALARM_QUEUE", msg);
    }

    public void sendSnmpAlarm(Object msg) {
        rabbitTemplate.convertAndSend("SNMP_ALARM_QUEUE", msg);
    }

    public void sendSnmpSyncAlarm(Object msg){
        rabbitTemplate.convertAndSend("SNMP_SYNC_ALARM_QUEUE", msg);
    }
}
