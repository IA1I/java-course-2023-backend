package edu.java.scrapper.service.kafka;

import edu.java.scrapper.dto.proto.LinkUpdateRequestProto;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import edu.java.scrapper.service.MessageSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;

@Log4j2
public class ScrapperQueueProducer implements MessageSender {
    private final KafkaTemplate<String, LinkUpdateRequestProto.LinkUpdateRequest> kafkaTemplate;

    public ScrapperQueueProducer(KafkaTemplate<String, LinkUpdateRequestProto.LinkUpdateRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(LinkUpdateRequest update) {
        LinkUpdateRequestProto.LinkUpdateRequest.Builder builder = LinkUpdateRequestProto.LinkUpdateRequest.newBuilder()
            .setId(update.id())
            .setDescription(update.description())
            .setUrl(update.url());
        for (int i = 0; i < update.tgChatIds().length; i++) {
            builder.addTgChatIds(update.tgChatIds()[i]);
        }
        LinkUpdateRequestProto.LinkUpdateRequest message = builder.build();

        try {
            kafkaTemplate.send("messages.protobuf.update", 0, "update", message);
        } catch (Exception e) {
            log.error("Error occurred during sending to Kafka", e);
        }
    }
}
