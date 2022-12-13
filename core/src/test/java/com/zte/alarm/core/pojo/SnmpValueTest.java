package com.zte.alarm.core.pojo;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author wshmang@163.com
 * @date 2021/3/26 9:27
 */
public class SnmpValueTest extends TestCase {

    @Test
    public void testSetOid() {

        SnmpValue snmpValue = new SnmpValue("1.3.6.1.4.1.3902.4101.1.3.1.3.50", "value");

        assertEquals("1.3.6.1.4.1.3902.4101.1.3.1.3", snmpValue.getColumnOid());
    }
}