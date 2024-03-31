//package edu.java.bot.command;
//
//import com.pengrad.telegrambot.model.Chat;
//import com.pengrad.telegrambot.model.Message;
//import com.pengrad.telegrambot.model.Update;
//import com.pengrad.telegrambot.model.User;
//import com.pengrad.telegrambot.request.SendMessage;
//import java.util.Map;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class UntrackCommandTest {
//
//    String LINE_SEPARATOR = System.lineSeparator();
//
//    @Autowired
//    @Qualifier("untrackCommand")
//    private Command untrackCommand;
//    @Autowired
//    @Qualifier("trackCommand")
//    private Command trackCommand;
//    @Autowired
//    @Qualifier("listCommand")
//    private Command listCommand;
//    @Autowired
//    @Qualifier("startCommand")
//    private Command startCommand;
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
//        Mockito.when(chatMock.id()).thenReturn(8L);
//        Mockito.when(messageMock.from()).thenReturn(userMock);
//        Mockito.when(userMock.id()).thenReturn(9L);
//    }
//
//    @Test
//    @Order(1)
//    void shouldReturnSendMessageFromUntrackCommandForNotRegisteredUser() {
//        SendMessage actual = untrackCommand.handle(updateMock);
//        Map<String, Object> expected = Map.of("chat_id", 8L, "text", "You are not registered");
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//
//    @Test
//    @Order(2)
//    void shouldReturnSendMessageFromUntrackCommandForRegisteredUserForEmptyLink() {
//        startCommand.handle(updateMock);
//        Mockito.when(messageMock.text()).thenReturn("/untrack");
//
//        SendMessage actual = untrackCommand.handle(updateMock);
//        Map<String, Object> expected = Map.of("chat_id", 8L, "text", "Incorrect use of command" + LINE_SEPARATOR +
//            "Use" + LINE_SEPARATOR +
//            "/untrack link" + LINE_SEPARATOR +
//            "/untrack link link...");
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//
//    @Test
//    @Order(3)
//    void shouldReturnSendMessageFromUntrackCommandForRegisteredUserForIncorrectInput() {
//        Mockito.when(messageMock.text()).thenReturn("/untrack github.com");
//
//        SendMessage actual = untrackCommand.handle(updateMock);
//        Map<String, Object> expected = Map.of("chat_id", 8L, "text", "github.com - invalid URL" + LINE_SEPARATOR);
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//
//    @Test
//    @Order(4)
//    void shouldReturnSendMessageFromUntrackCommandForRegisteredUserForCorrectInputWithNotTrackedLink() {
//        Mockito.when(messageMock.text()).thenReturn("/track https://github.com/");
//        trackCommand.handle(updateMock);
//        Mockito.when(messageMock.text()).thenReturn("/untrack https://stackoverflow.com/");
//
//        SendMessage actual = untrackCommand.handle(updateMock);
//        Map<String, Object> expected =
//            Map.of("chat_id", 8L, "text", "https://stackoverflow.com/ - untracked" + LINE_SEPARATOR);
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//
//    @Test
//    @Order(5)
//    void shouldReturnSendMessageFromUntrackCommandForRegisteredUserForCorrectInput() {
//        Mockito.when(messageMock.text()).thenReturn("/untrack https://github.com/");
//
//        SendMessage actual = untrackCommand.handle(updateMock);
//        Map<String, Object> expected =
//            Map.of("chat_id", 8L, "text", "https://github.com/ - untracked" + LINE_SEPARATOR);
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
//}
