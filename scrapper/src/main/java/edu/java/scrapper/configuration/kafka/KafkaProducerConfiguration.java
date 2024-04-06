package edu.java.scrapper.configuration.kafka;

import edu.java.scrapper.serdes.LinkUpdateRequestSerializer;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import static edu.java.scrapper.dto.proto.LinkUpdateRequestProto.LinkUpdateRequest;

@Configuration
@EnableKafka
public class KafkaProducerConfiguration {

    @Bean
    public KafkaTemplate<String, LinkUpdateRequest> protobufMessageKafkaTemplate(
        KafkaProducerProperties kafkaProducerProperties
    ) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers(),
            ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerProperties.getClientId(),
            ProducerConfig.ACKS_CONFIG, kafkaProducerProperties.getAcksMode(),
            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) kafkaProducerProperties.getDeliveryTimeout().toMillis(),
            ProducerConfig.LINGER_MS_CONFIG, kafkaProducerProperties.getLingerMs(),
            ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerProperties.getBatchSize(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LinkUpdateRequestSerializer.class
        )));
    }
}
