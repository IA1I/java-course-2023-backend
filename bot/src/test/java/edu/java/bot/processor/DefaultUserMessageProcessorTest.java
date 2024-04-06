package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.ListCommand;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DefaultUserMessageProcessorTest {
    String LINE_SEPARATOR = System.lineSeparator();
    static List<Command> commands;
    static UserMessageProcessor processor;

    static Update updateMock = Mockito.mock(Update.class);
    static Message messageMock = Mockito.mock(Message.class);
    static Chat chatMock = Mockito.mock(Chat.class);
    static User userMock = Mockito.mock(User.class);

    @BeforeAll
    static void preparation() {
        Command command = new ListCommand(null, null);
        commands = List.of(command);
        processor = new DefaultUserMessageProcessor(commands);

        Mockito.when(updateMock.message()).thenReturn(messageMock);
        Mockito.when(messageMock.chat()).thenReturn(chatMock);
        Mockito.when(chatMock.id()).thenReturn(10L);
        Mockito.when(messageMock.from()).thenReturn(userMock);
        Mockito.when(userMock.id()).thenReturn(11L);
    }

    @Test
    void shouldReturnListOfCommands() {
        List<Command> expected = commands;

        List<Command> actual = processor.commands();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void shouldReturnSendMessageForUnknownCommand() {
        Mockito.when(messageMock.text()).thenReturn("unknown command");
        Map<String, Object> expected = Map.of("chat_id", 10L, "text", "Unknown command");

        SendMessage actual = processor.process(updateMock);

        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
    }

//    @Test
//    void shouldReturnSendMessageForKnownCommand() {
//        Mockito.when(messageMock.text()).thenReturn("/list");
//
//        Map<String, Object> expected = Map.of("chat_id", 10L, "text", "You are not registered");
//
//        SendMessage actual = processor.process(updateMock);
//
//        Assertions.assertThat(actual.getParameters()).containsExactlyInAnyOrderEntriesOf(expected);
//    }
}
