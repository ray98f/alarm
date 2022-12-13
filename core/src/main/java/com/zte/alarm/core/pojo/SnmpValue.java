package com.zte.alarm.core.pojo;

import lombok.Data;

/**
 * @author wshmang@163.com
 * @date 2021/3/26 9:25
 */
@Data
public class SnmpValue {

    private String oid;

    private String columnOid;

    private String value;

    public SnmpValue(String oid, String value){

        this.oid = oid;
        this.value = value;
        this.columnOid = oid.substring(0, oid.lastIndexOf("."));
    }
}
