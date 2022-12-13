package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.codec.DecoderResult;
import io.netty.util.internal.StringUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wshmang@163.com
 * @date 2021/3/8 15:24
 */
@Data
public class GjkzMessage   {

    private GjkzFixedHeader gjkzFixedHeader;
    private Object variableHeader;
    private Object payload;
    private DecoderResult decoderResult;

    public GjkzMessage(){

    }

    public GjkzMessage(GjkzFixedHeader gjkzFixedHeader) {
        this(gjkzFixedHeader, (Object)null, (Object)null);
    }

    public GjkzMessage(GjkzFixedHeader gjkzFixedHeader, Object variableHeader) {
        this(gjkzFixedHeader, variableHeader, (Object)null);
    }

    public GjkzMessage(GjkzFixedHeader gjkzFixedHeader, Object variableHeader, Object payload) {
        this(gjkzFixedHeader, variableHeader, payload, DecoderResult.SUCCESS);
    }

    public GjkzMessage(GjkzFixedHeader gjkzFixedHeader, Object variableHeader, Object payload, DecoderResult decoderResult) {
        this.gjkzFixedHeader = gjkzFixedHeader;
        this.variableHeader = variableHeader;
        this.payload = payload;
        this.decoderResult = decoderResult;
    }
}
