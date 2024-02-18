package edu.java.bot.processors;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
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

    @Autowired
    @Lazy
    public DefaultUserMessageProcessor(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        Optional<Command> command = commands.stream()
            .filter(c -> c.supports(update))
            .findFirst();
        if (command.isEmpty()) {
            log.info("Unknown command {} from {}", update.message().text(), update.message().chat().id());
            return new SendMessage(update.message().chat().id(), "Unknown command");
        } else {
            log.info(
                "{} command from chat: {} user: {}",
                update.message().text(),
                update.message().chat().id(),
                update.message().from().firstName()
            );
            return command.get().handle(update);
        }
    }
}
