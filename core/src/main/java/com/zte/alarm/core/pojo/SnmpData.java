package com.zte.alarm.core.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wshmang@163.com
 * @date 2021/3/25 14:34
 */
@Data
public class SnmpData {

    private List<Map<String, SnmpValue>> data;
    private int columnSize;
    private int rowSize;

    public SnmpData(int columnSize, int rowSize) {
        this.columnSize = columnSize;
        this.rowSize = rowSize;
        this.data = new ArrayList<>();
        for (int i = 0; i < rowSize; i++) {
            data.add(new HashMap<>());
        }
    }

    public void set(int rowIndex, String columnKey, SnmpValue value) {
        Map<String, SnmpValue> row = data.get(rowIndex);

        row.putIfAbsent(columnKey, value);
    }

    public SnmpValue get(int rowIndex, int columnKey) {
        return data.get(rowIndex).get(columnKey);
    }

    public Map<String, SnmpValue> getRow(int rowIndex) {
        return data.get(rowIndex);
    }
}
