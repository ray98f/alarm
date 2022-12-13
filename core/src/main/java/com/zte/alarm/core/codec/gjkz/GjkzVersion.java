package com.zte.alarm.core.codec.gjkz;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 17:25
 */
@Slf4j
public enum GjkzVersion {
    //告警控制_1
    GJKZ_1("GJKZ", (byte) 1);

    private final String name;
    private final byte level;

    private GjkzVersion(String protocolName, byte protocolLevel) {
        this.name = (String) ObjectUtil.checkNotNull(protocolName, "协议名称");
        this.level = protocolLevel;
    }

    public String protocolName() {
        return this.name;
    }

    public byte[] protocolNameBytes() {
        return this.name.getBytes(CharsetUtil.UTF_8);
    }

    public byte protocolLevel() {
        return this.level;
    }

    public static GjkzVersion fromProtocolNameAndLevel(String protocolName, byte protocolLevel) {
        GjkzVersion mv = null;
        switch (protocolLevel) {
            case 1:
                mv = GJKZ_1;
                break;
            default:
                log.error(protocolName + "[" + protocolLevel + "]未知的协议级别");
                return null;
        }

        if (mv == null) {
            log.error(protocolName + "[" + protocolLevel + "]未知的协议名称");
        } else if (mv.name.equals(protocolName)) {
            return mv;
        } else {
            log.error(protocolName + "[" + protocolLevel + "]协议名称和级别不匹配");
        }
        return null;
    }
}
