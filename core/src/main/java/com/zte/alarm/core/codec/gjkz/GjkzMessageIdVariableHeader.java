package com.zte.alarm.core.codec.gjkz;

import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/11 8:51
 */
@Getter
@ToString
public class GjkzMessageIdVariableHeader {
    private final int messageId;

    public static GjkzMessageIdVariableHeader from(int messageId) {
        if (messageId >= 1 && messageId <= 65535) {
            return new GjkzMessageIdVariableHeader(messageId);
        } else {
            throw new IllegalArgumentException("messageId: " + messageId + " (expected: 1 ~ 65535)");
        }
    }

    protected GjkzMessageIdVariableHeader(int messageId) {
        this.messageId = messageId;
    }

}
