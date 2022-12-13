package com.zte.alarm.core.pojo;

import java.io.Serializable;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:46
 */
public class BaseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int sequence;

    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
