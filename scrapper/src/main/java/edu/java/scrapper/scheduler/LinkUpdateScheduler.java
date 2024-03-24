package edu.java.scrapper.scheduler;

import edu.java.scrapper.service.LinkUpdater;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@SuppressWarnings("LineLength")
public class LinkUpdateScheduler {

    private final LinkUpdater linkUpdater;

    public LinkUpdateScheduler(LinkUpdater linkUpdater) {
        this.linkUpdater = linkUpdater;
    }

    @Scheduled(
        fixedDelayString = "#{@'app-edu.java.scrapper.configuration.ApplicationConfig'.scheduler().interval().getSeconds()}",
        timeUnit = TimeUnit.SECONDS)
    public void update() {
        int updatesCount = linkUpdater.update();
        log.info("Updated {} links", updatesCount);
    }
}
