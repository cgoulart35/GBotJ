package com.stormerg.gbotj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class GBotJ {

    public static void main(final String[] args) {
        // Run SpringApplication
        ConfigurableApplicationContext context = SpringApplication.run(GBotJ.class, args);
    }
}

