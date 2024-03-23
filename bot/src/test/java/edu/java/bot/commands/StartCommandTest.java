package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StartCommandTest {
    String LINE_SEPARATOR = System.lineSeparator();
    @Autowired
    @Qualifier("startCommand")
    private Command startCommand;

    @Test
    void shouldReturnSendMessageFromStartCommandForNotRegisteredUser() {
        Update updateMock = Mockito.mock(Update.class);
        Message messageMock = Mockito.mock(Message.class);
        Chat chatMock = Mockito.mock(Chat.class);
        User userMock = Mockito.mock(User.class);

        Mockito.when(updateMock.message()).thenReturn(messageMock);
        Mockito.when(messageMock.chat()).thenReturn(chatMock);
        Mockito.when(chatMock.id()).thenReturn(4L);
        Mockito.when(messageMock.from()).thenReturn(userMock);
        Mockito.when(userMock.id()).thenReturn(5L);
        Mockito.when(userMock.firstName()).thenReturn("John");

        SendMessage actual = startCommand.handle(updateMock);
        Map<String, Object> expected =
            Map.of("chat_id", 4L, "text", "Hello, John" + LINE_SEPARATOR + "You are registered!");

        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
