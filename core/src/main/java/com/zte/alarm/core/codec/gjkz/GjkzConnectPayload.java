package com.zte.alarm.core.codec.gjkz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:03
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzConnectPayload {

    private String clientIdentifier;
    private String userName;
    private byte[] password;

    public GjkzConnectPayload(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }
}
