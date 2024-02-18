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
public class StartCommand extends AbstractCommand {
    public StartCommand(UserMessageProcessor processor, Dao<User, Long> userDao) {
        super(processor, userDao);
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Register a user";
    }

    @Override
    public SendMessage handle(Update update) {
        saveUser(update);
        long chatId = update.message().chat().id();

        return new SendMessage(chatId, getText(update));
    }

    private String getText(Update update) {
        StringBuilder text = new StringBuilder();
        text.append("Hello, ")
            .append(update.message().from().firstName())
            .append(LINE_SEPARATOR)
            .append("You are registered!");

        log.info("Created text with successful registration for: /start");
        return text.toString();
    }

    private void saveUser(Update update) {
        long id = update.message().from().id();
        User user = new User(id);
        userDao.save(user);

        log.info("Saved user {}", id);
    }
}
