package com.zte.alarm.core.service.snmp;

import com.zte.alarm.core.pojo.SnmpData;
import com.zte.alarm.core.util.SnmpUtil;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.*;

import java.io.IOException;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 9:42
 */
@Slf4j
public class SnmpServer {

    private Snmp snmp;

    private CommunityTarget target;

    private String targetIP;

    private String listenIP;

    private String community;

    private int lineCode;

    private int systemCode;

    private ThreadPool threadPool;

    private MultiThreadedMessageDispatcher dispatcher;

    private Address listenAddress;

    private CommandResponder commandResponder;

    private SnmpCommand snmpCommand;

    public SnmpServer(String targetIP, String listenIP, String community, int lineCode, int systemCode) {
        this.targetIP = targetIP;
        this.listenIP = listenIP;
        this.community = community;
        this.lineCode = lineCode;
        this.systemCode = systemCode;
    }

    public void bind() throws IOException {

        target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(GenericAddress.parse(targetIP));
        target.setVersion(SnmpConstants.version2c);
        target.setTimeout(3000L);
        target.setRetries(3);
        log.info("community:{},target:{}", community, targetIP);

        threadPool = ThreadPool.create("trap", 2);
        dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
        listenAddress = GenericAddress.parse(listenIP);
        TransportMapping transport;
        if (listenAddress instanceof UdpAddress) {
            transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
        } else {
            transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
        }
        snmp = new Snmp(dispatcher, transport);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        snmp.listen();
        snmp.addCommandResponder(commandResponder);
        log.info("SNMP服务已启动,正在监听中... {}", listenIP);
    }

    public void setCommandResponder(CommandResponder commandResponder) {
        this.commandResponder = commandResponder;
    }

    public void send(PDU pdu) throws IOException {
        ResponseEvent resposePdu = snmp.send(pdu, target);
        log.info("resposePdu:{}", resposePdu);

    }

    public SnmpData getTable(String oid, String lowerBound, String upperBound) {
        try {
            TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());
            List<TableEvent> events = tUtils.getTable(target, new OID[]{new OID(oid)}, new OID(lowerBound), new OID(upperBound));
            SnmpData snmpData = SnmpUtil.getSnmpTable(events);
            return snmpData;
        } catch (Exception ex) {
            log.error("获取{} {}-{}表数据时出错", oid, lowerBound, upperBound);
            return null;
        }
    }


    public SnmpCommand getSnmpCommand() {
        return snmpCommand;
    }

    public void setSnmpCommand(SnmpCommand snmpCommand) {
        this.snmpCommand = snmpCommand;
    }

    public int getLineCode() {
        return lineCode;
    }

    public int getSystemCode() {
        return systemCode;
    }
}
