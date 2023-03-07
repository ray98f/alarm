package com.zte.snmp.service;

import com.zte.snmp.config.SnmpSetting;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

public interface IBaseCommandImpl {

    SnmpSetting getSnmpSetting();

    default Integer stationToCode(String stationName) {
        return getSnmpSetting().getStationInfo().get(stationName);
    }

    default LocalDateTime getTime(String time) {
        if (StringUtils.isEmpty(time)) {
            return LocalDateTime.of(2000, 1, 1, 0, 0, 0);
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
        return LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    }
}
