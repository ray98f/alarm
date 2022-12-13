package com.zte.alarm.core.util;

/**
 * @author wshmang@163.com
 * @date 2021/3/12 13:56
 */
public class CommonUtil {

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String getChineseUTF_8(String octetString) {
        try {
            String temps[];
            byte bs[];
            try {
                if (!octetString.contains(":")) {
                    return octetString;
                }
            } catch (Exception e) {
                return null;
            }
            temps = octetString.split(":");
            bs = new byte[temps.length];
            for (int i = 0; i < temps.length; i++) {
                bs[i] = (byte) Integer.parseInt(temps[i], 16);
            }

            return new String(bs, "utf-8");
        } catch (Exception ex) {
            return null;
        }
    }


    public static String getChineseGBK(String octetString) {
        try {
            String temps[] = octetString.split(":");
            byte bs[] = new byte[temps.length];
            for (int i = 0; i < temps.length; i++) {
                bs[i] = (byte) Integer.parseInt(temps[i], 16);
            }
            return new String(bs, "GB2312");
        } catch (Exception e) {
            return null;
        }
    }
}
