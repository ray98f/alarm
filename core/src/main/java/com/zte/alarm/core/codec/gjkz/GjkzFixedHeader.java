package com.zte.alarm.core.codec.gjkz;


import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 15:31
 */
public class GjkzFixedHeader {

    private final GjkzMessageType messageType;

    private final int remainingLength;

    public GjkzFixedHeader(GjkzMessageType messageType,int remainingLength) {
        this.messageType = (GjkzMessageType) ObjectUtil.checkNotNull(messageType, "messageType");
        this.remainingLength = remainingLength;
    }

    public GjkzMessageType messageType() {
        return this.messageType;
    }

    public int remainingLength() {
        return this.remainingLength;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "messageType=" + this.messageType + ']';
    }
}
