package edu.java.scrapper.scheduler;

import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@SuppressWarnings("LineLength")
public class LinkUpdateScheduler {

    @Scheduled(
        fixedDelayString = "#{@'app-edu.java.scrapper.configuration.ApplicationConfig'.scheduler().interval().getSeconds()}",
        timeUnit = TimeUnit.SECONDS)
    public void update() {
        log.info("Updated");
    }
}
