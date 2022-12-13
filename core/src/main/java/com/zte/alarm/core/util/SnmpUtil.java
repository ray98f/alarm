package com.zte.alarm.core.util;

import com.zte.alarm.core.pojo.SnmpData;
import com.zte.alarm.core.pojo.SnmpValue;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import java.util.List;

/**
 * @author wshmang@163.com
 * @date 2021/3/24 17:17
 */
public class SnmpUtil {


    private final static int columnIndex = 1;

    private final static int rowIndex = 2;


    public static int getColumnNum(TableEvent tableEvent) throws Exception {
        int[] index = tableEvent.getIndex().getValue();
        if (index.length < columnIndex + 1) {
            throw new Exception("无法获取到列");
        }
        return index[columnIndex];
    }

    public static int getRowNum(TableEvent tableEvent) throws Exception {
        int[] index = tableEvent.getIndex().getValue();
        if (index.length < rowIndex + 1) {
            throw new Exception("无法获取到行");
        }
        return index[rowIndex];
    }

    public static SnmpData getSnmpTable(List<TableEvent> tableEvents) throws Exception {

        int maxColumnSize = 0;
        int maxRowSize = 0;
        for (TableEvent event : tableEvents) {
            int columnNum = getColumnNum(event);
            int rowNum = getRowNum(event);

            if (columnNum > maxColumnSize) {
                maxColumnSize = columnNum;
            }
            if (rowNum > maxRowSize) {
                maxRowSize = rowNum;
            }
        }

        SnmpData snmpData = new SnmpData(maxColumnSize, maxRowSize);

        for (TableEvent event : tableEvents) {
            if (event.isError()) {
                continue;
            }
            int rowIndex = getRowNum(event) - 1;

            if (event.getColumns().length > 1) {
                throw new Exception("表事件列长度大于1,与预期不符.");
            }
            for (VariableBinding vb : event.getColumns()) {
                String oid = vb.getOid().toString();
                String value = vb.getVariable().toString();
                SnmpValue snmpValue = new SnmpValue(oid, value);
                snmpData.set(rowIndex, snmpValue.getColumnOid(), snmpValue);
            }
        }
        return snmpData;
    }
}
