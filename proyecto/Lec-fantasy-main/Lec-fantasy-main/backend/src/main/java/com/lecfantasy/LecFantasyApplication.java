package com.lecfantasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
@EnableScheduling
public class LecFantasyApplication {

    public static void main(String[] args) {
        // Todas las fechas se guardan en UTC; los clientes las muestran en hora local.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(LecFantasyApplication.class, args);
    }
}
