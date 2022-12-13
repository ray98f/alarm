package com.zte.alarm.core.exception;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:50
 */
public class SocketTimeoutException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SocketTimeoutException() {
        super();
    }

    public SocketTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketTimeoutException(String message) {
        super(message);
    }

    public SocketTimeoutException(Throwable cause) {
        super(cause);
    }
}
