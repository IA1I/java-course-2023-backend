package edu.java.scrapper.configuration.kafka;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("kafka")
public class KafkaProducerProperties {
    private String bootstrapServers;
    private String clientId;
    private String acksMode;
    private Duration deliveryTimeout;
    private String lingerMs;
    private String batchSize;
}
