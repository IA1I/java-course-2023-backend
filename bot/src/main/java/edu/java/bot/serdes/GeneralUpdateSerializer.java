package edu.java.bot.serdes;

import edu.java.bot.dto.proto.LinkUpdateRequestProto;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;

public class GeneralUpdateSerializer implements Serializer<Object> {
    @Override
    public byte[] serialize(String topic, Object data) {
        if (data instanceof LinkUpdateRequestProto.LinkUpdateRequest) {
            LinkUpdateRequestProto.LinkUpdateRequest update = (LinkUpdateRequestProto.LinkUpdateRequest) data;
            return update.toByteArray();
        } else {
            ByteArraySerializer byteArraySerializer = new ByteArraySerializer();
            return byteArraySerializer.serialize(topic, (byte[]) data);
        }
    }
}
