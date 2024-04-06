package edu.java.bot.configuration.kafka;

import edu.java.bot.serdes.LinkUpdateRequestDeserializer;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import static edu.java.bot.dto.proto.LinkUpdateRequestProto.LinkUpdateRequest;

@Configuration
@Log4j2
@EnableKafka
public class KafkaConsumerConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> containerFactory(
        KafkaConsumerProperties properties,
        CommonErrorHandler commonErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers(),
            ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId(),
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset(),
            ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, properties.getMaxPollIntervalMs(),
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.getEnableAutoCommit(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, LinkUpdateRequestDeserializer.class.getName()
        )));
        factory.setConcurrency(properties.getConcurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(commonErrorHandler);

        return factory;
    }
}
