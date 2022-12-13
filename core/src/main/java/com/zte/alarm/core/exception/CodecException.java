package com.zte.alarm.core.exception;

/**
 * @author wshmang@163.com
 * @date 2021/3/12 9:05
 */
public class CodecException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CodecException() {
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }
}
