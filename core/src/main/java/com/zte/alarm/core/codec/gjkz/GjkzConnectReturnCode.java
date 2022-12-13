package com.zte.alarm.core.codec.gjkz;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:23
 */
public enum GjkzConnectReturnCode {

    CONNECTION_ACCEPTED((byte)0),
    CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION((byte)1),
    CONNECTION_REFUSED_IDENTIFIER_REJECTED((byte)2),
    CONNECTION_REFUSED_SERVER_UNAVAILABLE((byte)3),
    CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD((byte)4),
    CONNECTION_REFUSED_NOT_AUTHORIZED((byte)5);

    private static final Map<Byte, GjkzConnectReturnCode> VALUE_TO_CODE_MAP;
    private final byte byteValue;

    private GjkzConnectReturnCode(byte byteValue) {
        this.byteValue = byteValue;
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public static GjkzConnectReturnCode valueOf(byte b) {
        if (VALUE_TO_CODE_MAP.containsKey(b)) {
            return (GjkzConnectReturnCode)VALUE_TO_CODE_MAP.get(b);
        } else {
            throw new IllegalArgumentException("unknown connect return code: " + (b & 255));
        }
    }

    static {
        Map<Byte, GjkzConnectReturnCode> valueMap = new HashMap();
        GjkzConnectReturnCode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            GjkzConnectReturnCode code = var1[var3];
            valueMap.put(code.byteValue, code);
        }

        VALUE_TO_CODE_MAP = Collections.unmodifiableMap(valueMap);
    }
}
