package edu.java.bot.configuration.kafka;

import edu.java.bot.serdes.GeneralUpdateSerializer;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaDLQConfiguration {
    @Bean
    public ProducerFactory<Integer, byte[]> producerFactory(
        KafkaConsumerProperties properties,
        DLQProperties dlqProperties
    ) {
        return new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers(),
            ProducerConfig.LINGER_MS_CONFIG, dlqProperties.getLingerMs(),
            ProducerConfig.ACKS_CONFIG, dlqProperties.getAcksMode(),
            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) dlqProperties.getDeliveryTimeout().toMillis(),
            ProducerConfig.BATCH_SIZE_CONFIG, dlqProperties.getBatchSize(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralUpdateSerializer.class
        ));
    }

    @Bean
    public KafkaTemplate<Integer, byte[]> kafkaTemplate(ProducerFactory<Integer, byte[]> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
        KafkaTemplate<Integer, byte[]> template,
        DLQProperties dlqProperties
    ) {
        return new DeadLetterPublishingRecoverer(
            template,
            ((consRec, exception) -> new TopicPartition(dlqProperties.getTopic(), consRec.partition()))
        );
    }

    @Bean
    public CommonErrorHandler commonErrorHandler(DeadLetterPublishingRecoverer deadLetterRecoverer) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterRecoverer);
        errorHandler.addNotRetryableExceptions(ValidationException.class, ConstraintViolationException.class);
        return errorHandler;
    }
}
