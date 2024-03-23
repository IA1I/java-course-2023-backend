package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrackCommandTest {

    String LINE_SEPARATOR = System.lineSeparator();

    @Autowired
    @Qualifier("trackCommand")
    private Command trackCommand;
    @Autowired
    @Qualifier("listCommand")
    private Command listCommand;
    @Autowired
    @Qualifier("startCommand")
    private Command startCommand;

    static Update updateMock = Mockito.mock(Update.class);
    static Message messageMock = Mockito.mock(Message.class);
    static Chat chatMock = Mockito.mock(Chat.class);
    static User userMock = Mockito.mock(User.class);

    @BeforeAll
    static void preparation() {
        Mockito.when(updateMock.message()).thenReturn(messageMock);
        Mockito.when(messageMock.chat()).thenReturn(chatMock);
        Mockito.when(chatMock.id()).thenReturn(6L);
        Mockito.when(messageMock.from()).thenReturn(userMock);
        Mockito.when(userMock.id()).thenReturn(7L);
    }

    @Test
    @Order(1)
    void shouldReturnSendMessageFromTrackCommandForNotRegisteredUser() {
        SendMessage actual = trackCommand.handle(updateMock);
        Map<String, Object> expected = Map.of("chat_id", 6L, "text", "You are not registered");

        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    @Order(2)
    void shouldReturnSendMessageFromTrackCommandForRegisteredUserForEmptyLink() {
        startCommand.handle(updateMock);
        Mockito.when(messageMock.text()).thenReturn("/track");

        SendMessage actual = trackCommand.handle(updateMock);
        Map<String, Object> expected = Map.of("chat_id", 6L, "text", "Incorrect use of command" + LINE_SEPARATOR +
            "Use" + LINE_SEPARATOR +
            "/track link" + LINE_SEPARATOR +
            "/track link link...");

        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    @Order(3)
    void shouldReturnSendMessageFromTrackCommandForRegisteredUserForIncorrectInput() {
        Mockito.when(messageMock.text()).thenReturn("/track github.com");

        SendMessage actual = trackCommand.handle(updateMock);
        Map<String, Object> expected = Map.of("chat_id", 6L, "text", "github.com - invalid URL" + LINE_SEPARATOR);

        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    @Order(4)
    void shouldReturnSendMessageFromTrackCommandForRegisteredUserForCorrectInput() {
        Mockito.when(messageMock.text()).thenReturn("/track https://github.com/");

        SendMessage actual = trackCommand.handle(updateMock);
        Map<String, Object> expected = Map.of("chat_id", 6L, "text", "https://github.com/ - tracked" + LINE_SEPARATOR);

        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
