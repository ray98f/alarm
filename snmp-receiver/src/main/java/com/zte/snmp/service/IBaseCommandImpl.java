package com.zte.snmp.service;

import com.zte.snmp.config.SnmpSetting;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public interface IBaseCommandImpl {

    SnmpSetting getSnmpSetting();

    default Integer stationToCode(String stationName) {
        return getSnmpSetting().getStationInfo().get(stationName);
    }

    default LocalDateTime getTime(String time) {
        if (StringUtils.isEmpty(time)) {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return LocalDateTime.parse(now, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        String[] strings = time.split(",");
        /**
         *  "2023-2-22,17:42:0.0,+8:0"
         */
        if (strings.length == 3) {
            return OffsetDateTime.of(LocalDate.parse(strings[0], DateTimeFormatter.ofPattern("yyyy-M-d")),
                LocalTime.parse(strings[1], DateTimeFormatter.ofPattern("HH:m:s[.n]")),
                ZoneOffset.of(strings[2].split(":")[0])).toLocalDateTime();
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return LocalDateTime.parse(now, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
