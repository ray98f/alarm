package com.zte.snmp.service;

import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.snmp.config.SnmpSetting;
import com.zte.snmp.constant.U31Constants;
import com.zte.snmp.constant.U31Constants.Alarm;
import com.zte.snmp.constant.U31Constants.Type;
import com.zte.snmp.util.MessageSender;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

@Slf4j
@RequiredArgsConstructor
public class U31CommandResponderImpl implements IBaseCommandImpl, CommandResponder {

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
                    log.info("OID :{} \t MSG:{}", oidStr, binding.getVariable().toString());
                    if (oidStr.startsWith(Alarm.AlarmCreateTimePrefix)) {
                        timeStr = binding.getVariable().toString();
                    }
                }
                OID type = new OID(pdu.getVariable(new OID(U31Constants.KeyType)).toString());
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
                            Arrays.asList(new Heartbeat(lineCode, systemCode, LocalDateTime.now())));
                        break;
                    default:
                }
                if (dto != null) {
                    log.info("{}", dto);
//                    messageSender.sendSnmpAlarm(Collections.singletonList(dto));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private SnmpAlarm getAlarmDto(PDU pdu, String timeStr) {
        String alarmCodeName = pdu.getVariable(new OID(Alarm.AlarmCodeName)).toString();
        String alarmCode = pdu.getVariable(new OID(Alarm.AlarmCode)).toString();
        log.info("告警码名称:{}", alarmCodeName);
        String alarmManagedObjectInstanceName = pdu.getVariable(new OID(Alarm.AlarmManagedObjectInstanceName))
            .toString();
        String alarmSpecificProblem = pdu.getVariable(new OID(Alarm.AlarmSpecificProblem)).toString();
        String alarmNetType = pdu.getVariable(new OID(Alarm.AlarmNetType)).toString();
        SnmpAlarm dto = new SnmpAlarm(lineCode, systemCode, false, alarmManagedObjectInstanceName, alarmSpecificProblem,
            alarmCode, alarmNetType, getTime(timeStr),
            null, null);
        return dto;
    }

}
