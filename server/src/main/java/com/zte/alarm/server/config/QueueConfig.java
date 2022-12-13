package com.zte.alarm.server.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wshmang@163.com
 * @date 2021/3/22 16:48
 */
@Configuration
public class QueueConfig {

    @Bean
    public Queue heartbeat() {
        return new Queue("HEARTBEAT_QUEUE", true);
    }

    @Bean
    public Queue alarmQueue() {
        return new Queue("ALARM_QUEUE", true);
    }

    @Bean
    public Queue syncAlarmQueue() {
        return new Queue("SYNC_ALARM_QUEUE", true);
    }

    @Bean
    public Queue snmpAlarmQueue() {
        return new Queue("SNMP_ALARM_QUEUE", true);
    }

    @Bean
    public Queue snmpSyncAlarmQueue() {
        return new Queue("SNMP_SYNC_ALARM_QUEUE", true);
    }

    @Bean
    public Queue refresh() {
        return new Queue("REFRESH_QUEUE", true);
    }
}
