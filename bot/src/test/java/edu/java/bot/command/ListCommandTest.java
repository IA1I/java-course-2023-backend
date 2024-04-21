//package edu.java.bot.command;
//
//import com.pengrad.telegrambot.model.Chat;
//import com.pengrad.telegrambot.model.Message;
//import com.pengrad.telegrambot.model.Update;
//import com.pengrad.telegrambot.model.User;
//import com.pengrad.telegrambot.request.SendMessage;
//import edu.java.bot.client.scrapper.LinkClient;
//import edu.java.bot.dto.response.LinkResponse;
//import edu.java.bot.dto.response.ListLinksResponse;
//import java.util.List;
//import java.util.Map;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@AutoConfigureMockMvc
//public class ListCommandTest {
//    String LINE_SEPARATOR = System.lineSeparator();
//
//    @Autowired
//    @Qualifier("listCommand")
//    private Command listCommand;
//    @Autowired
//    @Qualifier("startCommand")
//    private Command startCommand;
//    @Autowired
//    @Qualifier("trackCommand")
//    private Command trackCommand;
//    @MockBean
//    private LinkClient linkClient;
//
//    static Update updateMock = Mockito.mock(Update.class);
//    static Message messageMock = Mockito.mock(Message.class);
//    static Chat chatMock = Mockito.mock(Chat.class);
//    static User userMock = Mockito.mock(User.class);
//
//    @BeforeAll
//    static void preparation() {
//        Mockito.when(updateMock.message()).thenReturn(messageMock);
//        Mockito.when(messageMock.chat()).thenReturn(chatMock);
//        Mockito.when(chatMock.id()).thenReturn(2L);
//        Mockito.when(messageMock.from()).thenReturn(userMock);
//        Mockito.when(userMock.id()).thenReturn(3L);
//        Mockito.when(messageMock.text()).thenReturn("/track https://github.com/");
//    }
//
//    @Test
//    void should() {
//        ListLinksResponse response = new ListLinksResponse(List.of(new LinkResponse(1L, "https://github.com/")), 1);
//        Mono<ListLinksResponse> mono = Mono.just(response);
//        Mockito.when(linkClient.getAllLinks(3L)).thenReturn(mono);
//
//        StepVerifier.create(mono)
//            .expectNext(response)
//            .verifyComplete();
//    }
//
//    @Test
//    void should2() {
//        ListLinksResponse response = new ListLinksResponse(List.of(new LinkResponse(1L, "https://github.com/")), 1);
//        Mono mock = Mockito.mock(Mono.class);
//        Mono<ListLinksResponse> mono = Mono.just(response);
//        Mockito.when(linkClient.getAllLinks(3L)).thenReturn(mock);
//        Mockito.when(mock.block()).thenReturn(response);
//
//        StepVerifier.create(mono)
//            .expectNext(response)
//            .verifyComplete();
//        SendMessage message = listCommand.handle(updateMock);
//        System.out.println(message);
//    }
//
//    @Test
//    @Order(1)
//    void shouldReturnSendMessageFromListCommandForNotRegisteredUser() {
//        SendMessage actual = listCommand.handle(updateMock);
//        Map<String, Object> expected = Map.of("chat_id", 2L, "text", "You are not registered");
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//
//    @Test
//    @Order(2)
//    void shouldReturnSendMessageFromListCommandForRegisteredUser() {
//        startCommand.handle(updateMock);
//
//        SendMessage actual = listCommand.handle(updateMock);
//        Map<String, Object> expected = Map.of("chat_id", 2L, "text", "You are not tracking links");
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//
//    @Test
//    @Order(3)
//    void shouldReturnSendMessageFromListCommandForRegisteredUserWithTrackedLink() {
//        trackCommand.handle(updateMock);
//
//        SendMessage actual = listCommand.handle(updateMock);
//        Map<String, Object> expected = Map.of("chat_id", 2L, "text", "https://github.com/" + LINE_SEPARATOR);
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//}
