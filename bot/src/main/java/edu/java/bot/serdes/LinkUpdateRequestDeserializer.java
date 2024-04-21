package edu.java.bot.serdes;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import static edu.java.bot.dto.proto.LinkUpdateRequestProto.LinkUpdateRequest;

@Log4j2
public class LinkUpdateRequestDeserializer implements Deserializer<LinkUpdateRequest> {

    private static final String ERROR_MESSAGE = "Error when deserializing byte[] to protobuf";

    @Override
    public LinkUpdateRequest deserialize(String topic, byte[] data) {
        try {
            return LinkUpdateRequest.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error(ERROR_MESSAGE, e);
            throw new SerializationException(ERROR_MESSAGE, e);
        }
    }
}
