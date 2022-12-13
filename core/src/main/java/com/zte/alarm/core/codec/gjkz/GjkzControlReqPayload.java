package com.zte.alarm.core.codec.gjkz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/11 13:52
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzControlReqPayload {
    private byte[] address;
    private byte[] value;
}
