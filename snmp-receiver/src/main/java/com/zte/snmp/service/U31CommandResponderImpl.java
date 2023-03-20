package com.zte.snmp.service;

import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.snmp.config.SnmpSetting;
import com.zte.snmp.constant.U31Constants;
import com.zte.snmp.constant.U31Constants.Alarm;
import com.zte.snmp.constant.U31Constants.Type;
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
                    log.debug("OID :{} \t MSG:{}", oidStr, binding.getVariable().toString());
                    if (oidStr.startsWith(Alarm.AlarmCreateTimePrefix)) {
                        timeStr = binding.getVariable().toString();
                    }
                }
                OID type = new OID(pdu.getVariable(new OID(U31Constants.KeyType)).toString());
                SnmpAlarm dto = null;
                switch (type.format()) {
                    case Type.CreateAlarm:
                        log.debug(systemCode + "系统,告警产生");
                        dto = getAlarmDto(pdu, timeStr);
                        if (dto != null) {
                            dto.setCleared(false);
                        }
                        break;
                    case Type.RecoverAlarm:
                        log.debug(systemCode + "系统,告警恢复");
                        dto = getAlarmDto(pdu, timeStr);
                        if (dto != null) {
                            dto.setCleared(true);
                        }
                        break;
                    case Type.Heart:
                        log.debug(systemCode + "系统,心跳");
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
        String alarmCode = pdu.getVariable(new OID(U31Constants.Alarm.AlarmCode)).toString();
        // 盐盆站,机架=1,机框=1,槽位=8,端口=3
        String alarmManagedObjectInstanceName = StringUtil.hexToString(
                pdu.getVariable(new OID(U31Constants.Alarm.AlarmManagedObjectInstanceName)).toString().replaceAll(":", ""),
                Charset.forName("GBK"));
        try {
            String[] strings = alarmManagedObjectInstanceName.split(",");
            if (strings.length >= 4) {
                String _alarmManagedObjectInstanceName = strings[1] + "," + strings[2] + "," + strings[3];
                String alarmSpecificProblem = StringUtil.hexToString(
                        pdu.getVariable(new OID(U31Constants.Alarm.AlarmSpecificProblem)).toString().replaceAll(":", ""),
                        Charset.forName("GBK"));
                String stationName = StringUtil.hexToString(
                        pdu.getVariable(new OID(U31Constants.Alarm.AlarmMocObjectInstance)).toString().replaceAll(":", ""),
                        Charset.forName("GBK"));
                Integer stationCode = stationToCode(stationName);
                if (stationCode == null) {
                    log.error("station.json 站点名称没有对应值：" + stationName);
                    return null;
                }
                String alarmEventType = pdu.getVariable(new OID(U31Constants.Alarm.AlarmEventType)).toString();
                return new SnmpAlarm(lineCode, systemCode, false, _alarmManagedObjectInstanceName,
                        alarmSpecificProblem, alarmCode, alarmEventType, getTime(timeStr), null, stationCode);
            }
            return null;
        } catch (Exception e) {
            log.error("错误非标字符串:{},{},", alarmManagedObjectInstanceName, e.getMessage(), e);
            throw e;
        }
    }
}
