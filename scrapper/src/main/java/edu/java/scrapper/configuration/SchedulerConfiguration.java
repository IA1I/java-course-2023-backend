package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.dto.proto.LinkUpdateRequestProto;
import edu.java.scrapper.scheduler.LinkUpdateScheduler;
import edu.java.scrapper.service.LinkUpdater;
import edu.java.scrapper.service.MessageSender;
import edu.java.scrapper.service.http.BotClientSender;
import edu.java.scrapper.service.kafka.ScrapperQueueProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "app.scheduler", name = "enable", havingValue = "true")
public class SchedulerConfiguration {

    @Bean
    public LinkUpdateScheduler linkUpdateScheduler(LinkUpdater linkUpdater) {
        return new LinkUpdateScheduler(linkUpdater);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
    public MessageSender scrapperQueueProducer(
        KafkaTemplate<String, LinkUpdateRequestProto.LinkUpdateRequest> kafkaTemplate
    ) {
        return new ScrapperQueueProducer(kafkaTemplate);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
    public MessageSender botClientSender(BotClient botClient) {
        return new BotClientSender(botClient);
    }
}
