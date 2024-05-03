package com.stormerg.gbotj;

import com.stormerg.gbotj.services.discord.DiscordService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GBotJ {

    public static void main(final String[] args) {
        // Run SpringApplication
        ConfigurableApplicationContext context = SpringApplication.run(GBotJ.class, args);

        // Initialize DiscordService
        DiscordService discordService = context.getBean(DiscordService.class);
        new Thread(discordService::init, "DiscordService Thread").start();
    }
}

