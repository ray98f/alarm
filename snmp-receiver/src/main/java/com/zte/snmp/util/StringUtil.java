package com.zte.snmp.util;

import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    public String hexToString(String hexString, Charset charset) {
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            // 每两个十六进制字符对应一个字节
            int hex = Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
            // 强制类型转换为字节
            byteArray[i] = (byte) hex;
        }
        // 将字节数组转换为GBK编码的字符串
        return new String(byteArray, charset);
    }
}
