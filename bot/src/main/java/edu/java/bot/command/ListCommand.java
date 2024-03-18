package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.Dao;
import edu.java.bot.dto.User;
import edu.java.bot.processor.UserMessageProcessor;
import io.mola.galimatias.URL;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ListCommand extends AbstractCommand {
    public ListCommand(UserMessageProcessor processor, Dao<User, Long> userDao) {
        super(processor, userDao);
    }

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Show list of tracked links";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();

        return new SendMessage(chatId, getText(update));
    }

    private String getText(Update update) {
        long id = update.message().from().id();
        if (!isRegistered(id)) {
            log.info("Created text with not registered user for: /list");
            return "You are not registered";
        }

        List<URL> urls = userDao.get(id).getUrls();
        if (urls.isEmpty()) {
            log.info("Created text with empty tracked links for: /list");
            return "You are not tracking links";
        } else {
            StringBuilder text = new StringBuilder();
            for (URL url : urls) {
                text.append(url)
                    .append(LINE_SEPARATOR);
            }

            log.info("Created text with tracked links for: /list");
            return text.toString();
        }
    }
}
