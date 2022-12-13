package com.zte.alarm.server.queue;

import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.alarm.server.runner.ServerRunner;
import com.zte.alarm.server.runner.TelSnmpServerRunner;
import com.zte.alarm.server.runner.TransferSnmpServerRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/22 17:18
 */

@Component
@Slf4j
public class MessageReceiver {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ServerRunner serverRunner;

    @Autowired
    private TelSnmpServerRunner telSnmpServerRunner;

    @Autowired
    private TransferSnmpServerRunner transferSnmpServerRunner;

    @RabbitListener(queues = "REFRESH_QUEUE")
    public void processRefresh() {
        // 向tcp客户端发送告警列表同步命令
        serverRunner.getServer().refresh();

        // 向公务电话snmp 获取告警列表
        List<SnmpAlarm> telSnmpAlarms = telSnmpServerRunner.getSnmpServer().getSnmpCommand().getAlarmList();
        if (telSnmpAlarms != null && telSnmpAlarms.size() > 0) {
            messageSender.sendSnmpSyncAlarm(telSnmpAlarms);
        }

        // 向传输snmp 获取告警列表
        List<SnmpAlarm> transferSnmpAlarms = transferSnmpServerRunner.getSnmpServer().getSnmpCommand().getAlarmList();
        if (transferSnmpAlarms != null && transferSnmpAlarms.size() > 0) {
            messageSender.sendSnmpSyncAlarm(transferSnmpAlarms);
        }
    }

}
