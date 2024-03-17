package edu.java.scrapper.configuration;

import edu.java.scrapper.scheduler.LinkUpdateScheduler;
import edu.java.scrapper.service.LinkUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {

    @Bean
    public LinkUpdateScheduler linkUpdateScheduler(LinkUpdater linkUpdater) {
        return new LinkUpdateScheduler(linkUpdater);
    }
}
