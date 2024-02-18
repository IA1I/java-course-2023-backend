package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.Dao;
import edu.java.bot.dto.User;
import edu.java.bot.processors.UserMessageProcessor;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UntrackCommand extends AbstractCommand {
    public UntrackCommand(UserMessageProcessor processor, Dao<User, Long> userDao) {
        super(processor, userDao);
    }

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Stop tracking a link";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();

        return new SendMessage(chatId, getText(update));
    }

    private String getText(Update update) {
        long id = update.message().from().id();
        if (!isRegistered(id)) {
            log.info("Created text with not registered user for: /untrack");
            return "You are not registered";
        }
        String[] links = update.message().text().split(" ");
        if (links.length == 1) {
            log.info("Created text with instruction for: /untrack");
            return "Incorrect use of command" + LINE_SEPARATOR
                + "Use" + LINE_SEPARATOR
                + "/untrack link" + LINE_SEPARATOR
                + "/untrack link link...";
        } else {
            return processLinks(links, id);
        }
    }

    private String processLinks(String[] links, long id) {
        StringBuilder text = new StringBuilder();
        for (int i = 1; i < links.length; i++) {
            text.append(links[i])
                .append(" - ");

            try {
                URL url = URL.parse(links[i]);
                User user = userDao.get(id);
                if (user.contains(url)) {
                    log.info("Removed url: {} for user: {}", url, id);
                    user.removeUrl(url);
                }
                text.append("untracked");
            } catch (GalimatiasParseException e) {
                log.info("Invalid URL: {} for user: {}", links[i], id);
                text.append("invalid URL");
            }
            text.append(LINE_SEPARATOR);
        }

        return text.toString();
    }
}
