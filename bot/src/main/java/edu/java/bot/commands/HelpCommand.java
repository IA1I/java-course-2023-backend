package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.Dao;
import edu.java.bot.dto.User;
import edu.java.bot.processors.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class HelpCommand extends AbstractCommand {
    public HelpCommand(UserMessageProcessor processor, Dao<User, Long> userDao) {
        super(processor, userDao);
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "Display command window";
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
