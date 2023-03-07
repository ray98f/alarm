package com.zte.snmp.service;

import com.zte.snmp.config.SnmpSetting;
import com.zte.snmp.config.SnmpSetting.Item;
import com.zte.snmp.util.MessageSender;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SNMPRunner {

    private final SnmpSetting snmpSetting;
    private final MessageSender messageSender;

    @PostConstruct
    public void inti() throws IOException {
        log.info("启动SNMP转化器");
        ThreadPool threadPool = ThreadPool.create("trap", 2);
        MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool,
            new MessageDispatcherImpl());
        SnmpSetting config = snmpSetting;
        for (Item item : config.getConfig()) {
            Address address = GenericAddress.parse(item.getListen());
            TransportMapping transport;
            if (address instanceof UdpAddress) {
                transport = new DefaultUdpTransportMapping((UdpAddress) address);
            } else {
                transport = new DefaultTcpTransportMapping((TcpAddress) address);
            }
            Snmp snmp = new Snmp(dispatcher, transport);
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
            switch (item.getType()) {
                case UME:
                    snmp.addCommandResponder(
                        new UMECommandResponderImpl(item.getLineCode(), item.getSystemCode(), messageSender,
                            snmpSetting));
                    break;
                case U31:
                    snmp.addCommandResponder(
                        new U31CommandResponderImpl(item.getLineCode(), item.getSystemCode(), messageSender,
                            snmpSetting));
                    break;
            }
            snmp.listen();
        }
    }
}
