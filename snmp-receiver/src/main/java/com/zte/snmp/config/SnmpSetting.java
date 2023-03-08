package com.zte.snmp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "snmp")
public class SnmpSetting {

    private Item config;
    private Map<String, Integer> stationInfo;

    public enum Type {
        UME, U31
    }

    @Setter
    @Getter
    @Accessors(chain = true)
    @NoArgsConstructor
    public static class Item {

        private Type type;
        private Integer lineCode;
        private Integer systemCode;
        private String listen;
        private String community;

    }

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String stationFile = System.getenv("STATION_FILE");
        String s;
        if (!StringUtil.isNullOrEmpty(stationFile)) {
            s = new String(Files.readAllBytes(Paths.get(stationFile)));
        } else {
            Path path = Paths.get("./station.json");
            if (Files.exists(path)) {
                s = new String(Files.readAllBytes(path));
            } else {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("station.json")));
                s = reader.lines().reduce("", (s1, s2) -> s1 + s2);
            }
        }
        this.stationInfo = objectMapper.readValue(s, LinkedHashMap.class);
    }

}
