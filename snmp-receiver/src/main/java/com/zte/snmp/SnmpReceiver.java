package com.zte.snmp;

import com.zte.snmp.config.SnmpSetting;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

@Slf4j
@SpringBootApplication
public class SnmpReceiver {


    public static void main(String[] args) throws IOException {
        SpringApplication.run(SnmpReceiver.class);
    }

    private static SnmpSetting getConfig() throws IOException {
        InputStream inputStream = null;
        String configFile = System.getenv("CONFIG_FILE");
        Path path = Paths.get("./config.yml");
        if (configFile != null) {
            inputStream = Files.newInputStream(Paths.get(configFile));
        } else if (Files.exists(path)) {
            inputStream = Files.newInputStream(path.toFile().toPath());
        } else {
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("./config.yml");
        }
        Yaml yaml = new Yaml();
        yaml.setBeanAccess(BeanAccess.FIELD);
        return yaml.loadAs(inputStream, SnmpSetting.class);
    }

}
