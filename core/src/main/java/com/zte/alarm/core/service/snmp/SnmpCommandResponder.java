package com.zte.alarm.core.service.snmp;

import com.zte.alarm.core.listener.EventBehavior;
import com.zte.alarm.core.listener.SnmpMessageEventListener;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 13:48
 */
public abstract class SnmpCommandResponder implements CommandResponder {

    protected List<EventListener> eventListeners = new ArrayList<>();

    /**
     * SNMP 数据处理
     *
     * @param commandResponderEvent
     */
    @Override
    public abstract void processPdu(CommandResponderEvent commandResponderEvent);

    public void addEventListener(EventListener listener) {
        this.eventListeners.add(listener);
    }

    public List<EventListener> getEventListeners() {
        return eventListeners;
    }

    public void doMessageEvent(final Object msg) {
        try {
            for (EventListener listener : getEventListeners()) {
                if (listener instanceof SnmpMessageEventListener) {
                    EventBehavior eventBehavior = ((SnmpMessageEventListener) listener).message(msg);
                    if (EventBehavior.BREAK.equals(eventBehavior)) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
        }
    }
}
