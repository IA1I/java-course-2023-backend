package edu.java.scrapper.serdes;

import org.apache.kafka.common.serialization.Serializer;
import static edu.java.scrapper.dto.proto.LinkUpdateRequestProto.LinkUpdateRequest;

public class LinkUpdateRequestSerializer implements Serializer<LinkUpdateRequest> {
    @Override
    public byte[] serialize(String topic, LinkUpdateRequest data) {
        return data.toByteArray();
    }
}
