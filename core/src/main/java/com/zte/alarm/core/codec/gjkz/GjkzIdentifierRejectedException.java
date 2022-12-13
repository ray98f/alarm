package com.zte.alarm.core.codec.gjkz;

import io.netty.handler.codec.DecoderException;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 18:19
 */
public final class GjkzIdentifierRejectedException extends DecoderException {
    private static final long serialVersionUID = -1323503322689614981L;

    public GjkzIdentifierRejectedException() {
    }

    public GjkzIdentifierRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public GjkzIdentifierRejectedException(String message) {
        super(message);
    }

    public GjkzIdentifierRejectedException(Throwable cause) {
        super(cause);
    }
}
