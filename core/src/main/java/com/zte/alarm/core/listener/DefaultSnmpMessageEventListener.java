package com.zte.alarm.core.listener;

import com.zte.alarm.core.pojo.Heartbeat;
import com.zte.alarm.core.pojo.Refresh;
import com.zte.alarm.core.pojo.SnmpAlarm;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 13:41
 */
public class DefaultSnmpMessageEventListener implements SnmpMessageEventListener {
    @Override
    public EventBehavior message(Object msg) throws Exception {
        if (msg instanceof Heartbeat) {
            heartbeat((Heartbeat) msg);
        } else if (msg instanceof SnmpAlarm) {
            snmpAlarm((SnmpAlarm) msg);
        } else if (msg instanceof Refresh) {
            refresh();
        }

        return EventBehavior.CONTINUE;
    }

    public void refresh() throws Exception {

    }

    public void snmpAlarm(SnmpAlarm snmpAlarm) {

    }


    public void heartbeat(Heartbeat heartbeat) {

    }
}
