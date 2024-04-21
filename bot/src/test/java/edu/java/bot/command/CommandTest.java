package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class CommandTest {

    @Test
    void shouldReturnFalseForNullUpdate() {
        Command command = new HelpCommand(null);

        Assertions.assertThat(command.supports(null)).isFalse();
    }

    @Test
    void shouldReturnFalseForUpdateWithNullMessage() {
        Command command = new HelpCommand(null);
        Update update = new Update();

        Assertions.assertThat(command.supports(update)).isFalse();
    }

    @Test
    void shouldReturnFalseForUpdateWithMessageWithNullText() {
        Command command = new HelpCommand(null);
        Update updateMock = Mockito.mock(Update.class);
        Message message = new Message();
        Mockito.when(updateMock.message()).thenReturn(message);

        Assertions.assertThat(command.supports(updateMock)).isFalse();
    }

    @Test
    void shouldReturnTrueForTextStartsWithHelp() {
        Command command = new HelpCommand(null);
        Update updateMock = Mockito.mock(Update.class);
        Message messageMock = Mockito.mock(Message.class);
        Mockito.when(updateMock.message()).thenReturn(messageMock);
        Mockito.when(messageMock.text()).thenReturn("/help");

        Assertions.assertThat(command.supports(updateMock)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("testCasesForTestCommandName")
    void shouldReturnBotCommandWithCommandNameOfCommand(
        Command command,
        String expectedCommand
    ) {
        BotCommand botCommand = command.toApiCommand();
        String actualCommand = botCommand.command();

        Assertions.assertThat(actualCommand).isEqualTo(expectedCommand);
    }

    static Stream<Arguments> testCasesForTestCommandName() {
        return Stream.of(
            Arguments.of(new HelpCommand(null), "/help"),
            Arguments.of(new ListCommand(null, null), "/list"),
            Arguments.of(new StartCommand(null, null), "/start"),
            Arguments.of(new TrackCommand(null, null), "/track"),
            Arguments.of(new UntrackCommand(null, null), "/untrack")
        );
    }

    @ParameterizedTest
    @MethodSource("testCasesForTestCommandDescription")
    void shouldReturnBotCommandWithDescriptionOfCommand(
        Command command,
        String expectedDescription
    ) {
        BotCommand botCommand = command.toApiCommand();
        String actualDescription = botCommand.description();

        Assertions.assertThat(actualDescription).isEqualTo(expectedDescription);
    }

    static Stream<Arguments> testCasesForTestCommandDescription() {
        return Stream.of(
            Arguments.of(new HelpCommand(null), "Display command window"),
            Arguments.of(new ListCommand(null, null), "Show list of tracked links"),
            Arguments.of(new StartCommand(null, null), "Register a user"),
            Arguments.of(new TrackCommand(null, null), "Start tracking link"),
            Arguments.of(new UntrackCommand(null, null), "Stop tracking a link")
        );
    }
}
