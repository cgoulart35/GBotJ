package com.stormerg.gbotj.services.discord.impl.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

    // TODO - remove example
    @Scheduled(fixedRate = 5000) // Execute every 5 seconds
    public void doTask() {
        // Your task logic here
        log.info("Task executed at fixed rate.");
    }
}
