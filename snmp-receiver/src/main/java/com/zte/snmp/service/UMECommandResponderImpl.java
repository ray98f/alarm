package com.zte.snmp.service;

import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.snmp.config.SnmpSetting;
import com.zte.snmp.constant.U31Constants;
import com.zte.snmp.constant.UMEConstants;
import com.zte.snmp.constant.UMEConstants.Alarm;
import com.zte.snmp.constant.UMEConstants.Type;
import com.zte.snmp.util.MessageSender;
import com.zte.snmp.util.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class UMECommandResponderImpl implements IBaseCommandImpl, CommandResponder {

    private final Integer lineCode;
    private final Integer systemCode;
    private final MessageSender messageSender;
    @Getter
    private final SnmpSetting snmpSetting;


    @Override
    public void processPdu(CommandResponderEvent event) {
        try {
            PDU pdu = event.getPDU();
            if (pdu != null) {
                String timeStr = null;
                for (VariableBinding binding : pdu.getVariableBindings()) {
                    String oidStr = binding.getOid().format();
                    log.debug("OID :{} \t MSG:{}", oidStr, binding.getVariable().toString());
                    if (oidStr.startsWith(Alarm.AlarmCreateTimePrefix)) {
                        timeStr = binding.getVariable().toString();
                    }
                }
                OID type = new OID(pdu.getVariable(new OID(UMEConstants.KeyType)).toString());
                log.debug("Time:{},消息类型:{}", pdu.getVariable(new OID(UMEConstants.KeyTime)).toString(),
                        type.format());
                SnmpAlarm dto = null;
                switch (type.format()) {
                    case Type.CreateAlarm:
                        log.debug("告警产生");
                        dto = getAlarmDto(pdu, timeStr);
                        if (dto != null) {
                            dto.setCleared(false);
                        }
                        break;
                    case Type.RecoverAlarm:
                        log.debug("告警恢复");
                        dto = getAlarmDto(pdu, timeStr);
                        if (dto != null) {
                            dto.setCleared(true);
                        }
                        break;
                    case Type.Heart:
                        messageSender.sendHeartbeat(
                                Collections.singletonList(new Heartbeat(lineCode, systemCode, LocalDateTime.now())));
                        break;
                    default:
                }
                if (dto != null) {
                    log.info("{}", dto);
                    messageSender.sendSnmpAlarm(Collections.singletonList(dto));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private SnmpAlarm getAlarmDto(PDU pdu, String timeStr) {
        String alarmCode = pdu.getVariable(new OID(Alarm.AlarmCode)).toString();
        // 海城站,OIXG4A[0-1-13]-10GE:1
        String alarmManagedObjectInstanceName = StringUtil.hexToString(
                pdu.getVariable(new OID(Alarm.AlarmManagedObjectInstanceName)).toString().replaceAll(":", ""),
                Charset.forName("GBK"));
        try {
            String[] strings = alarmManagedObjectInstanceName.split(",");
            if (strings.length == 2) {
                String _alarmManagedObjectInstanceName = strings[1].substring(0, strings[1].indexOf("]") + 1);
                String alarmSpecificProblem = StringUtil.hexToString(
                        pdu.getVariable(new OID(Alarm.AlarmSpecificProblem)).toString().replaceAll(":", ""),
                        Charset.forName("GBK"));
                String stationName = StringUtil.hexToString(
                        pdu.getVariable(new OID(U31Constants.Alarm.AlarmMocObjectInstance)).toString().replaceAll(":", ""),
                        Charset.forName("GBK"));
                String alarmEventType = pdu.getVariable(new OID(Alarm.AlarmEventType)).toString();
                return new SnmpAlarm(lineCode, systemCode, false, _alarmManagedObjectInstanceName,
                        alarmSpecificProblem, alarmCode, alarmEventType, getTime(timeStr), null, stationToCode(stationName));
            }
            return null;
        } catch (Exception e) {
            log.error("错误非标字符串:{},{},", alarmManagedObjectInstanceName, e.getMessage(), e);
            throw e;
        }
    }
}
