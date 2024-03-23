package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HelpCommandTest {
    String LINE_SEPARATOR = System.lineSeparator();

    @Autowired
    @Qualifier("helpCommand")
    private Command helpCommand;

    @Test
    void shouldReturnSendMessageFromHelpCommand() {
        Update updateMock = Mockito.mock(Update.class);
        Message messageMock = Mockito.mock(Message.class);
        Chat chatMock = Mockito.mock(Chat.class);
        Mockito.when(updateMock.message()).thenReturn(messageMock);
        Mockito.when(messageMock.chat()).thenReturn(chatMock);
        Mockito.when(chatMock.id()).thenReturn(1L);

        Map<String, Object> expected = Map.of("chat_id", 1L, "text", "/help - Display command window" + LINE_SEPARATOR +
            "/list - Show list of tracked links" + LINE_SEPARATOR +
            "/start - Register a user" + LINE_SEPARATOR +
            "/track - Start tracking link" + LINE_SEPARATOR +
            "/untrack - Stop tracking a link" + LINE_SEPARATOR);

        SendMessage actual = helpCommand.handle(updateMock);

        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
