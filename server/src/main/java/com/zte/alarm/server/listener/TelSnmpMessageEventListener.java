package com.zte.alarm.server.listener;

import com.zte.alarm.core.listener.DefaultSnmpMessageEventListener;
import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.alarm.core.service.snmp.SnmpServer;
import com.zte.alarm.server.queue.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 13:57
 */

@Slf4j
public class TelSnmpMessageEventListener extends DefaultSnmpMessageEventListener {

    private SnmpServer snmpServer;

    @Autowired
    private MessageSender messageSender;

    public TelSnmpMessageEventListener(SnmpServer snmpServer) {
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

        messageSender.sendSnmpAlarm(new ArrayList() {{
            add(snmpAlarm);
        }});
    }

    @Override
    public void refresh() throws Exception {
        log.debug("refresh event");

        List<SnmpAlarm> snmpAlarms = snmpServer.getSnmpCommand().getAlarmList();
        if (snmpAlarms != null && snmpAlarms.size() > 0) {
            messageSender.sendSnmpSyncAlarm(snmpAlarms);
        }

    }
}
