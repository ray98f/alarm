package com.zte.alarm.core.codec.gjkz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:23
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzConnAckVariableHeader {
    private final GjkzConnectReturnCode connectReturnCode;

}
