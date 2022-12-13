package com.zte.alarm.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author wshmang@163.com
 * @date 2021/3/22 15:10
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
