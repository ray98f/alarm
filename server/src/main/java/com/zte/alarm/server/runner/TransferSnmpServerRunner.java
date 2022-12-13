package com.zte.alarm.server.runner;

import com.zte.alarm.core.service.snmp.SnmpServer;
import com.zte.alarm.core.service.snmp.wenzhous2.TransferSnmpCommand;
import com.zte.alarm.core.service.snmp.wenzhous2.TransferSnmpCommandResponder;
import com.zte.alarm.server.listener.TransferSnmpMessageEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 14:06
 */
@Component
@Order(2)
@Slf4j
public class TransferSnmpServerRunner implements CommandLineRunner {

    @Value("${snmp.transfer.targetIP}")
    private String targetIP;

    @Value("${snmp.transfer.listenIP}")
    private String listenIP;

    @Value("${snmp.transfer.community}")
    private String community;

    @Value("${snmp.transfer.lineCode}")
    private int lineCode;

    @Value("${snmp.transfer.systemCode}")
    private int systemCode;

    @Value("${snmp.transfer.charset}")
    private String charset;

    private SnmpServer snmpServer;

    @Override
    public void run(String... args) throws Exception {
        log.info("正在启动传输SNMP服务...");
        snmpServer = new SnmpServer(targetIP, listenIP, community, lineCode, systemCode);

        TransferSnmpCommandResponder transferSnmpCommandResponder = new TransferSnmpCommandResponder(lineCode, systemCode);
        transferSnmpCommandResponder.addEventListener(new TransferSnmpMessageEventListener(snmpServer));

        snmpServer.setCommandResponder(transferSnmpCommandResponder);
        snmpServer.setSnmpCommand(new TransferSnmpCommand());

        snmpServer.bind();
    }

    public SnmpServer getSnmpServer(){
        return this.snmpServer;
    }
}