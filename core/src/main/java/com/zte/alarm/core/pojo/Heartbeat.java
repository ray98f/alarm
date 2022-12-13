package com.zte.alarm.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 10:01
 */
@Data
@AllArgsConstructor
public class Heartbeat implements Serializable {

    private int lineCode;

    private int systemCode;

    private LocalDateTime time;
}
