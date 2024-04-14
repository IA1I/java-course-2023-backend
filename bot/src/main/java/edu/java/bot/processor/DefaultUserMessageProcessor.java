package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DefaultUserMessageProcessor implements UserMessageProcessor {
    private final List<Command> commands;
    private final MeterRegistry meterRegistry;
    private final Counter messageCounter;

    @Autowired
    @Lazy
    public DefaultUserMessageProcessor(List<Command> commands, MeterRegistry meterRegistry) {
        this.commands = commands;
        this.meterRegistry = meterRegistry;
        this.messageCounter = meterRegistry.counter("number_of_processed_messages");
    }

    @Override
    public List<Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        Optional<Command> optionalCommand = commands.stream()
            .filter(c -> c.supports(update))
            .findFirst();
        messageCounter.increment();
        if (optionalCommand.isEmpty()) {
            log.info("Unknown command {} from {}", update.message().text(), update.message().chat().id());
            return new SendMessage(update.message().chat().id(), "Unknown command");
        } else {
            Command command = optionalCommand.get();

            meterRegistry.counter("number_of_commands_processed", "command_type", command.command())
                .increment();

            log.info(
                "{} command from chat: {} user: {}",
                update.message().text(),
                update.message().chat().id(),
                update.message().from().firstName()
            );
            return command.handle(update);
        }
    }
}
