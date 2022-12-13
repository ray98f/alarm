package com.zte.alarm.core.codec.gjkz;

import com.zte.alarm.core.service.north.server.Command;

/**
 * @author wshmang@163.com
 * @date 2021/3/26 11:04
 */
public class GjkzCommand implements Command {
    @Override
    public Object getRefreshCommand() {
        return GjkzMessageFactory.newMessage(new GjkzFixedHeader(GjkzMessageType.ALARMSYNCREQ, 0), null, null);
    }
}
