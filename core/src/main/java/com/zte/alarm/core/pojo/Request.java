package com.zte.alarm.core.pojo;

import java.io.Serializable;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:46
 */
public class Request extends BaseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object message;

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Request [sequence=");
        sb.append(sequence);
        sb.append(", message=");
        sb.append(message);
        sb.append("]");
        return sb.toString();
    }
}
