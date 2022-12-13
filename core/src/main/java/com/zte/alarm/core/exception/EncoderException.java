package com.zte.alarm.core.exception;

import io.netty.handler.codec.CodecException;

/**
 * @author wshmang@163.com
 * @date 2021/3/12 9:05
 */
public class EncoderException extends CodecException {
    private static final long serialVersionUID = 1L;

    public EncoderException() {
    }

    public EncoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncoderException(String message) {
        super(message);
    }

    public EncoderException(Throwable cause) {
        super(cause);
    }
}
