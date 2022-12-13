package com.zte.alarm.server.runner;

import com.zte.alarm.core.service.snmp.SnmpServer;
import com.zte.alarm.core.service.snmp.wenzhous2.TelSnmpCommand;
import com.zte.alarm.core.service.snmp.wenzhous2.TelSnmpCommandResponder;
import com.zte.alarm.server.listener.TelSnmpMessageEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 9:40
 */
@Component
@Order(1)
@Slf4j
public class TelSnmpServerRunner implements CommandLineRunner {

    @Value("${snmp.tel.targetIP}")
    private String targetIP;

    @Value("${snmp.tel.listenIP}")
    private String listenIP;

    @Value("${snmp.tel.community}")
    private String community;

    @Value("${snmp.transfer.lineCode}")
    private int lineCode;

    @Value("${snmp.transfer.systemCode}")
    private int systemCode;

    private SnmpServer snmpServer;

    @Override
    public void run(String... args) throws Exception {
        log.info("正在启动公务电话SNMP服务...");
        snmpServer = new SnmpServer(targetIP, listenIP, community, lineCode, systemCode);

        TelSnmpCommandResponder telSnmpCommandResponder = new TelSnmpCommandResponder(lineCode, systemCode);

        telSnmpCommandResponder.addEventListener(new TelSnmpMessageEventListener(snmpServer));

        snmpServer.setCommandResponder(telSnmpCommandResponder);

        snmpServer.setSnmpCommand(new TelSnmpCommand(snmpServer));

        snmpServer.bind();
    }

    public SnmpServer getSnmpServer() {
        return this.snmpServer;
    }
}
