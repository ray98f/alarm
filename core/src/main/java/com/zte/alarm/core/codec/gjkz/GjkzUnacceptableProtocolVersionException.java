package com.zte.alarm.core.codec.gjkz;

import io.netty.handler.codec.DecoderException;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 17:28
 */
public class GjkzUnacceptableProtocolVersionException  extends DecoderException {
    private static final long serialVersionUID = 4914652213232455749L;

    public GjkzUnacceptableProtocolVersionException() {
    }

    public GjkzUnacceptableProtocolVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GjkzUnacceptableProtocolVersionException(String message) {
        super(message);
    }

    public GjkzUnacceptableProtocolVersionException(Throwable cause) {
        super(cause);
    }
}
