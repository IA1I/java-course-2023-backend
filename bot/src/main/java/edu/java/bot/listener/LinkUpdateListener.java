package edu.java.bot.listener;

import edu.java.bot.dto.proto.LinkUpdateRequestProto;
import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class LinkUpdateListener {

    private static final String ERROR_MESSAGE = "Error occurred while processing the update";
    private final UpdateService updateService;

    public LinkUpdateListener(UpdateService updateService) {
        this.updateService = updateService;
    }

    @KafkaListener(topics = "messages.protobuf.update",
                   groupId = "messages.protobuf.update",
                   containerFactory = "containerFactory")
    public void handleMessage(
        LinkUpdateRequestProto.LinkUpdateRequest linkUpdateRequestProto,
        Acknowledgment acknowledgment
    ) {
        log.info("A new update has arrived: {}", linkUpdateRequestProto.toString());
        LinkUpdateRequest updateRequest = fromProtoToDto(linkUpdateRequestProto);

        try {
            updateService.sendUpdate(updateRequest);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
            throw new RuntimeException(ERROR_MESSAGE, e);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    private LinkUpdateRequest fromProtoToDto(LinkUpdateRequestProto.LinkUpdateRequest linkUpdateRequestProto) {
        return new LinkUpdateRequest(
            linkUpdateRequestProto.getId(),
            linkUpdateRequestProto.getUrl(),
            linkUpdateRequestProto.getDescription(),
            linkUpdateRequestProto.getTgChatIdsList().toArray(new Long[0])
        );
    }
}
