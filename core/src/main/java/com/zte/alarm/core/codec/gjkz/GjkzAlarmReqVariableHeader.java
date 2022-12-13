package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.pojo.Alarm;
import io.netty.util.internal.ObjectUtil;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:05
 */
@Getter
@ToString
public class GjkzAlarmReqVariableHeader extends GjkzMessageIdVariableHeader {
    private Alarm alarm;

    protected GjkzAlarmReqVariableHeader(int messageId, Alarm alarm) {
        super(messageId);
        this.alarm = ObjectUtil.checkNotNull(alarm, "告警信息不能为空");
    }
}
