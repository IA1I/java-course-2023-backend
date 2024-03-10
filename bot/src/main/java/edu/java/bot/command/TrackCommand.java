package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.Dao;
import edu.java.bot.dto.User;
import edu.java.bot.processor.UserMessageProcessor;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class TrackCommand extends AbstractCommand {
    public TrackCommand(UserMessageProcessor processor, Dao<User, Long> userDao) {
        super(processor, userDao);
    }

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "Start tracking link";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();

        return new SendMessage(chatId, getText(update));
    }

    private String getText(Update update) {
        long id = update.message().from().id();
        if (!isRegistered(id)) {
            log.info("Created text with not registered user for: /track");
            return "You are not registered";
        }
        String[] links = update.message().text().split(" ");
        if (links.length == 1) {
            log.info("Created text with instruction for: /track");
            return "Incorrect use of command" + LINE_SEPARATOR
                + "Use" + LINE_SEPARATOR
                + "/track link" + LINE_SEPARATOR
                + "/track link link...";
        } else {
            return processLinks(links, id);
        }
    }

    private String processLinks(String[] links, long id) {
        StringBuilder text = new StringBuilder();
        for (int i = 1; i < links.length; i++) {
            text.append(links[i])
                .append(" - ");

            processLink(id, text, links[i]);
            text.append(LINE_SEPARATOR);
        }

        return text.toString();
    }

    private void processLink(long id, StringBuilder text, String link) {
        try {
            URL url = URL.parse(link);
            User user = userDao.get(id);
            if (!user.contains(url)) {
                log.info("Added url: {} for user: {}", url, id);
                user.addUrl(url);
            }
            text.append("tracked");
        } catch (GalimatiasParseException e) {
            log.info("Invalid URL: {} for user: {}", link, id);
            text.append("invalid URL");
        }
    }
}
