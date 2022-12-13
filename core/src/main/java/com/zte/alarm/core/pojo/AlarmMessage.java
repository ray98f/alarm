package com.zte.alarm.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author wshmang@163.com
 * @date 2021/3/10 13:47
 */
@Getter
@AllArgsConstructor
@ToString
public class AlarmMessage implements Serializable {
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;
}
