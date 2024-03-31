package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class HelpCommand extends AbstractCommand {

    private static final String COMMAND_NAME = "/help";
    private static final String COMMAND_DESCRIPTION = "Display command window";

    public HelpCommand(UserMessageProcessor processor) {
        super(processor);
    }

    @Override
    public String command() {
        return COMMAND_NAME;
    }

    @Override
    public String description() {
        return COMMAND_DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();

        return new SendMessage(chatId, getText());
    }

    private String getText() {
        StringBuilder text = new StringBuilder();
        for (Command command : processor.commands()) {
            text.append(command.command())
                .append(" - ")
                .append(command.description())
                .append(LINE_SEPARATOR);
        }

        log.info("Created text with commands for: /help");
        return text.toString();
    }

}
