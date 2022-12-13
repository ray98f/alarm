package com.zte.alarm.core.pojo;

import java.io.Serializable;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:47
 */

public class Response extends BaseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int EXCEPTION = -1;

    public static final int SUCCESS = 0;

    private int code;
    private Object result;
    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Response [sequence=");
        sb.append(sequence);
        sb.append(", code=");
        sb.append(code);
        sb.append(", result=");
        sb.append(result);
        sb.append("]");
        return sb.toString();
    }
}