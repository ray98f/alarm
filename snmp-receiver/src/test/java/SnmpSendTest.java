// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SnmpSendTest.java

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpSendTest {

    public SnmpSendTest() {
        snmp = null;
        target = null;
    }

    private final String address = "udp:127.0.0.1/161";

    public void init()
        throws IOException {
        System.out.println("----< 初始 Trap 的IP和端口 >----");
        target = createTarget4Trap(address);
        target.setCommunity(new OctetString(""));
        TransportMapping transport = new DefaultTcpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    public void sendPDU()
        throws IOException {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.3902.4101.1.4.1.1"),
            new OctetString("SNMP Trap Test.see more:http://www.micmiu.com")));
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime,
            new TimeTicks((new UnsignedInteger32(System.currentTimeMillis() / 1000L)).getValue())));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(".1.3.6.1.4.1.3902.3400.4.16.3.7")));
        pdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.3902.3400.4.16.3.8.25.0"), new OctetString(
            "<ServiceAffecting,Unknown><VendorAlarmCode,100760>  <MEUserLabel,\u65B0\u5858\u7AD9><SubName,RSEB[0-1-4]-ETH:10>")));
        pdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.3902.3400.4.16.3.8.7.0.39"), new OctetString(
            "\u8BBE\u5907\u4E0E\u540E\u53F0\u7F51\u7BA1\u7684\u5171\u540C\u4F53\u9274\u6743\u914D\u7F6E\u4E0D\u5339\u914D\uFF0C\u5BFC\u81F4\u7F51\u7BA1\u4E0B\u53D1\u7684SNMP\u62A5\u6587\u65E0\u6CD5\u901A\u8FC7\u8BBE\u5907\u9A8C\u8BC1\uFF1B\u5176\u5B83\u7F51\u7BA1\u8BEF\u914D\u7F6E\u4E86\u8BE5\u8BBE\u5907\u7684IP\u5730\u5740\uFF0C\u5BF9\u8BE5\u8BBE\u5907\u53D1\u5305\u8BBF\u95EE\uFF0C\u7531\u4E8E\u914D\u7F6E\u7684\u5171\u540C\u4F53\u4E0D\u4E00\u81F4\uFF0C\u5BFC\u81F4\u8BBE\u5907\u4EA7\u751F\u8FD9\u6761\u544A\u8B66\uFF1B\u8BBE\u5907\u53D7\u5230\u7F51\u7EDC\u5DE5\u5177\u8F6F\u4EF6\u7684\u7AEF\u53E3\u626B\u63CF\u6216\u653B\u51FB\u3002")));
        pdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.3902.3400.4.16.3.8.5.0.39"), new OctetString("1")));
        pdu.setType(-89);
        snmp.send(pdu, target);
        System.out.println("----&gt; Trap Send END &lt;----");
    }

    public static CommunityTarget createTarget4Trap(String address) {
        CommunityTarget target = new CommunityTarget();
        target.setAddress(GenericAddress.parse(address));
        target.setVersion(1);
        target.setTimeout(3000L);
        target.setRetries(3);
        return target;
    }

    public static void main(String args[]) {
//        try {
//            SnmpSendTest demo = new SnmpSendTest();
//            demo.init();
//            demo.sendPDU();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
    private static LocalDateTime getTime(String time) {
        if (StringUtils.isEmpty(time)) {
            return LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        }
        String[] strings = time.split(",");
        /**
         *  "2023-2-22,17:42:0.0,+8:0"
         */
        if (strings.length == 3) {
            return OffsetDateTime.of(LocalDate.parse(strings[0], DateTimeFormatter.ofPattern("yyyy-M-dd")),
                LocalTime.parse(strings[1], DateTimeFormatter.ofPattern("[H]H:[m]m:s[.n]")),
                ZoneOffset.of(strings[2].split(":")[0])).toLocalDateTime();
        }
        return LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    }
    public static final int DEFAULT_VERSION = 1;
    public static final long DEFAULT_TIMEOUT = 3000L;
    public static final int DEFAULT_RETRY = 3;
    private Snmp snmp;
    private CommunityTarget target;
}
