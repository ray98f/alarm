package com.zte.alarm.server.listener;

import com.zte.alarm.core.listener.DefaultSnmpMessageEventListener;
import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.alarm.core.service.snmp.SnmpServer;
import com.zte.alarm.server.queue.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 13:57
 */

@Slf4j
public class TransferSnmpMessageEventListener extends DefaultSnmpMessageEventListener {

    private SnmpServer snmpServer;

    @Autowired
    private MessageSender messageSender;

    public TransferSnmpMessageEventListener(SnmpServer snmpServer) {
        this.snmpServer = snmpServer;
    }

    @Override
    public void heartbeat(Heartbeat heartbeat) {
        log.debug("heartbeat event:{}", heartbeat);

        messageSender.sendHeartbeat(new ArrayList() {{
            add(heartbeat);
        }});
    }

    @Override
    public void snmpAlarm(SnmpAlarm snmpAlarm) {
        log.debug("snmp alarm event:{}", snmpAlarm);

        messageSender.sendSyncAlarm(new ArrayList() {{
            add(snmpAlarm);
        }});
    }

    @Override
    public void refresh() throws Exception {
        log.debug("refresh event");

        //TODO 获取告警列表,数据入队
    }
}
