package com.zte.snmp.service;

import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.snmp.config.SnmpSetting;
import com.zte.snmp.constant.UMEConstants;
import com.zte.snmp.constant.UMEConstants.Alarm;
import com.zte.snmp.constant.UMEConstants.Type;
import com.zte.snmp.util.MessageSender;
import com.zte.snmp.util.StringUtil;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

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
                        dto.setCleared(false);
                        break;
                    case Type.RecoverAlarm:
                        log.debug("告警恢复");
                        dto = getAlarmDto(pdu, timeStr);
                        dto.setCleared(true);
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
        String alarmCodeName = pdu.getVariable(new OID(Alarm.AlarmCodeName)).toString();
        String alarmCode = pdu.getVariable(new OID(Alarm.AlarmCode)).toString();
        log.debug("告警Id:{}",
            Optional.ofNullable(pdu.getVariable(new OID(Alarm.AlarmId))).map(Variable::toString).orElse(null));
        log.debug("告警码名称:{}", alarmCodeName);
        //海城站,OIXG4A[0-1-13]-10GE:1
        String alarmManagedObjectInstanceName = StringUtil.hexToString(
            pdu.getVariable(new OID(Alarm.AlarmManagedObjectInstanceName)).toString().replaceAll(":", ""),
            Charset.forName("GBK"));
        try {
            String[] strings = alarmManagedObjectInstanceName.split(",");
            String stationName = strings[0];
            String _alarmManagedObjectInstanceName = alarmManagedObjectInstanceName.substring(0,
                alarmManagedObjectInstanceName.indexOf("]") + 1);
            _alarmManagedObjectInstanceName = _alarmManagedObjectInstanceName.substring(
                _alarmManagedObjectInstanceName.indexOf(",") + 1);

            String alarmSpecificProblem = StringUtil.hexToString(
                pdu.getVariable(new OID(Alarm.AlarmSpecificProblem)).toString().replaceAll(":", ""),
                Charset.forName("GBK"));
            /**
             *  告警类型作为网元类型
             */
            String alarmNetType = pdu.getVariable(new OID(Alarm.AlarmEventType)).toString();
            SnmpAlarm dto = new SnmpAlarm(lineCode, systemCode, false, _alarmManagedObjectInstanceName,
                alarmSpecificProblem, alarmCode, alarmNetType, getTime(timeStr), null, stationToCode(stationName));
            return dto;
        } catch (Exception e) {
            log.error("错误非标字符串:{},{},", alarmManagedObjectInstanceName, e.getMessage(), e);
            throw e;
        }

    }
}
