package com.zte.alarm.core.service.snmp.wenzhous2;

import com.zte.alarm.core.pojo.AlarmMessage;
import com.zte.alarm.core.pojo.SnmpAlarm;
import com.zte.alarm.core.pojo.SnmpData;
import com.zte.alarm.core.pojo.SnmpValue;
import com.zte.alarm.core.service.snmp.SnmpCommand;
import com.zte.alarm.core.service.snmp.SnmpServer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wshmang@163.com
 * @date 2021/3/26 10:51
 */
@Slf4j
public class TelSnmpCommand implements SnmpCommand {


    private SnmpServer snmpServer;

    public TelSnmpCommand(SnmpServer snmpServer) {
        this.snmpServer = snmpServer;
    }


    @Override
    public List<SnmpAlarm> getAlarmList() {
        SnmpData snmpData = snmpServer.getTable(TelSnmpConstants.CURRENT_ALARM_TABLE.toString(), TelSnmpConstants.CURRENT_ALARM_TABLE.getLowerBound(), TelSnmpConstants.CURRENT_ALARM_TABLE.getUpperBound());
        if (snmpData == null) {
            return null;
        }
        log.debug("get alarm list:{}", snmpData);

        List<SnmpAlarm> snmpAlarms = new ArrayList<>();
        for (int i = 0; i < snmpData.getRowSize(); i++) {
            Map<String, SnmpValue> row = snmpData.getRow(i);
            List<AlarmMessage> alarmMessageList = new ArrayList<>();
            for (Map.Entry<String, SnmpValue> entry : row.entrySet()) {
                TelSnmpConstants telSnmpConstants = TelSnmpConstants.from(entry.getKey());
                if (telSnmpConstants != null) {
                    alarmMessageList.add(new AlarmMessage(telSnmpConstants.getName(), entry.getValue().getValue()));
                }
            }

            //TODO 确认时间格式
            SnmpAlarm snmpAlarm = new SnmpAlarm(
                snmpServer.getLineCode(),
                snmpServer.getSystemCode(),
                false,
                row.get(TelSnmpConstants.ALARM_MANAGED_OBJECT_INSTANCE.toString()).getValue(),
                row.get(TelSnmpConstants.ALARM_SPECIFIC_PROBLEM.toString()).getValue(),
                row.get(TelSnmpConstants.ALARM_CODE.toString()).getValue(),
                row.get(TelSnmpConstants.ALARM_NETYPE.toString()).getValue(),
                LocalDateTime.parse(row.get(TelSnmpConstants.ALARM_EVENT_TIME.toString()).getValue(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                alarmMessageList, null);
            snmpAlarms.add(snmpAlarm);
        }
        return snmpAlarms;
    }
}
