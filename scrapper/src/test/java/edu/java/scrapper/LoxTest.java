//package edu.java.scrapper;
//
//import edu.java.scrapper.dto.proto.LinkUpdateRequestProto;
//import edu.java.scrapper.dto.request.LinkUpdateRequest;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//@SpringBootTest
//@Log4j2
//public class LoxTest {
//
//    @Autowired
//    private KafkaTemplate<String, LinkUpdateRequestProto.LinkUpdateRequest> kafkaTemplate;
//
//    @Test
//    void s() {
//        System.out.println(System.getProperty("os.name"));
//        System.out.println(System.getProperty("os.arch"));
//    }
//
//    @Test
//    void sh() throws ExecutionException, InterruptedException {
//        LinkUpdateRequest update = new LinkUpdateRequest(1L, "url", "description", new Long[] {440439212L});
//        LinkUpdateRequestProto.LinkUpdateRequest.Builder builder = LinkUpdateRequestProto.LinkUpdateRequest.newBuilder()
//            .setId(update.id())
//            .setDescription(update.description())
//            .setUrl(update.url());
//        for (int i = 0; i < update.tgChatIds().length; i++) {
//            builder.addTgChatIds(update.tgChatIds()[i]);
//        }
//        LinkUpdateRequestProto.LinkUpdateRequest message = builder.build();
//
//        kafkaTemplate.send("messages.protobuf", message);
////        CompletableFuture<SendResult<String, LinkUpdateRequestProto.LinkUpdateRequest>> send =
////
////        dumpResult(send.get());
//    }
//
//    void dumpResult(SendResult<?, ?> result) {
//        log.info("partition: {}, offset: {}", result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
//    }
//}
