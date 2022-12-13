package com.zte.alarm.core.service.snmp.wenzhous2;

import com.zte.alarm.core.pojo.AlarmMessage;
import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.Refresh;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.alarm.core.service.snmp.SnmpCommandResponder;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.VariableBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.zte.alarm.core.util.CommonUtil.getChineseGBK;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 9:44
 */
@Slf4j
public class TransferSnmpCommandResponder extends SnmpCommandResponder {

    private int lineCode;
    private int systemCode;

    public TransferSnmpCommandResponder(int lineCode, int systemCode) {
        this.lineCode = lineCode;
        this.systemCode = systemCode;
    }

    @Override
    public void processPdu(CommandResponderEvent commandResponderEvent) {

        if (commandResponderEvent == null || commandResponderEvent.getPDU() == null) {
            log.warn("[Warn] ResponderEvent or PDU is null");
            return;
        }
        String trapOIDValue = commandResponderEvent.getPDU().getVariable(SnmpConstants.snmpTrapOID).toString();
        log.trace("snmp processPdu trapOIDValue：{}", trapOIDValue);

        if (trapOIDValue.contains(TelSnmpConstants.HEARTBEAT.toString())) {
            heartbeat();
        } else if (trapOIDValue.contains(TelSnmpConstants.ALARM_NEW.toString())) {
            alarm(commandResponderEvent, false);
        } else if (trapOIDValue.contains(TelSnmpConstants.ALARM_CLEARED.toString())) {
            alarm(commandResponderEvent, true);
        } else if (trapOIDValue.contains(TelSnmpConstants.REBUILD.toString())) {
            refresh();
        } else {
            log.warn("未知的trap {}", commandResponderEvent.getPDU().getVariableBindings());
        }
    }

    private void alarm(CommandResponderEvent respEvnt, boolean isCleared) {
        LocalDateTime alarmTime = LocalDateTime.now();
        // 告警原因
        String alarmSpecificProblem = "";
        // 厂商告警码编号，该码值和alarmSystemType一起构成一个EMS中唯一的告警码。
        String emsAlarmCode = "";
        // 网元类型(Moc)
        String alarmNetype = "";
        // 告警位置名称（网元或网元内定位的名称）
        String alarmManagedObjectInstanceName = "";
        Vector recVBs = respEvnt.getPDU().getVariableBindings();
        List<AlarmMessage> alarmMessage = new ArrayList<>();
        for (int i = 0; i < recVBs.size(); i++) {
            VariableBinding recVB = (VariableBinding) recVBs.elementAt(i);
            String oid = recVB.getOid().toString();
            String value = recVB.getVariable().toString();
            String value2 = getChineseGBK(value);
            log.info("收到告警:{},{}", recVB.getVariable(), value);


            if (oid.contains(TelSnmpConstants.ALARM_EVENT_TIME.toString())) {
                alarmTime = TelSnmpConstants.getTime(value);
                if (value2 != null) {
                    alarmTime = TelSnmpConstants.getTime(value2);
                }
                continue;
            } else if (oid.contains(TelSnmpConstants.ALARM_MANAGED_OBJECT_INSTANCE_NAME.toString())) {
                alarmManagedObjectInstanceName = value;
                if (value2 != null) {
                    alarmManagedObjectInstanceName = value2;
                }
                continue;
            } else if (oid.contains(TelSnmpConstants.ALARM_NETYPE.toString())) {
                alarmNetype = value;
                if (value2 != null) {
                    alarmNetype = value2;
                }
                continue;
            } else if (oid.contains(TelSnmpConstants.ALARM_SPECIFIC_PROBLEM.toString())) {
                alarmSpecificProblem = value;
                if (value2 != null) {
                    alarmSpecificProblem = value2;
                }
                continue;
            } else if (oid.contains(TelSnmpConstants.ALARM_CODE.toString())) {
                emsAlarmCode = value;
                continue;
            } else {
                TelSnmpConstants telSnmpConstants = TelSnmpConstants.from(oid);
                if (telSnmpConstants != null) {
                    alarmMessage.add(new AlarmMessage(telSnmpConstants.getName(), value));
                } else {
                    log.warn("未配置的oid:{},value:{}", oid, value);
                }
            }
        }


        SnmpAlarm snmpAlarm = new SnmpAlarm(lineCode, systemCode, isCleared, alarmManagedObjectInstanceName, alarmSpecificProblem, emsAlarmCode, alarmNetype, alarmTime, alarmMessage);

        log.trace("snmp alarm:{}", snmpAlarm);

        doMessageEvent(snmpAlarm);
    }

    private void heartbeat() {
        Heartbeat heartbeat = new Heartbeat(lineCode, systemCode, LocalDateTime.now());
        log.trace("heartbeat:{}", heartbeat);
        doMessageEvent(heartbeat);
    }

    private void refresh() {
        doMessageEvent(new Refresh());
    }
}
