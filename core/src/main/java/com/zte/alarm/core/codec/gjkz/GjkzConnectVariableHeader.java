package com.zte.alarm.core.codec.gjkz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 16:02
 */
@Getter
@AllArgsConstructor
@ToString
public class GjkzConnectVariableHeader {

    private String name;
    private int version;
    private boolean hasUserName;
    private boolean hasPassword;
    private boolean flag5;
    private boolean flag4;
    private boolean flag3;
    private boolean flag2;
    private boolean flag1;
    private boolean flag0;
    private int keepAliveTimeSeconds;

}
