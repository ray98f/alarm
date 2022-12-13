package com.zte.alarm.core.codec.gjkz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 19:25
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzReadRespPayload {
    private byte[] value;
}
