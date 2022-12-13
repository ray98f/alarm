package com.zte.alarm.core.exception;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:50
 */
public class SocketRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SocketRuntimeException() {
        super();
    }

    public SocketRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketRuntimeException(String message) {
        super(message);
    }

    public SocketRuntimeException(Throwable cause) {
        super(cause);
    }

}
