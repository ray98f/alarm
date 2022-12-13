package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.pojo.AlarmMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:05
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzAlarmReqPayload {

    private List<AlarmMessage> alarmMessageList;
}
