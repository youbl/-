package com.beinet.resourcecapture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
public class ResourceCaptureApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceCaptureApplication.class, args);
    }

}
