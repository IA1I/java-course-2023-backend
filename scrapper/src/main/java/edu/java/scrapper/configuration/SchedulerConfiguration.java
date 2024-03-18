package edu.java.scrapper.configuration;

import edu.java.scrapper.scheduler.LinkUpdateScheduler;
import edu.java.scrapper.service.LinkUpdater;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "app.scheduler", name = "enable", havingValue = "true")
public class SchedulerConfiguration {

    @Bean
    public LinkUpdateScheduler linkUpdateScheduler(@Qualifier("jdbcLinksUpdaterService") LinkUpdater linkUpdater) {
        return new LinkUpdateScheduler(linkUpdater);
    }
}
