package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.pojo.Alarm;
import com.zte.alarm.core.pojo.AlarmList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 10:43
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzAlarmSyncRespPayload {

    /**
     * 告警信息
     */
    private AlarmList alarmList = new AlarmList();
}
