package edu.java.bot.configuration.kafka;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("dlq")
public class DLQProperties {
    private String topic;
    private Integer replications;
    private Integer partitions;
    private String acksMode;
    private Duration deliveryTimeout;
    private Integer lingerMs;
    private Integer batchSize;
}
