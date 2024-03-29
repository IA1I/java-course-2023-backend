package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.Bot;
import edu.java.bot.dto.request.LinkUpdateRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TgChatUpdateService implements UpdateService {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Bot bot;

    @Autowired
    public TgChatUpdateService(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void sendUpdate(LinkUpdateRequest updateRequest) {
        for (Long chatId : updateRequest.tgChatIds()) {
            StringBuilder message = new StringBuilder();
            message.append("Hello dear user!")
                .append(LINE_SEPARATOR)
                .append("Link ")
                .append(updateRequest.url())
                .append("is updated")
                .append(LINE_SEPARATOR)
                .append(updateRequest.description());

            log.info("Tg chat id {} with message:{}", chatId, message);
            bot.execute(new SendMessage(chatId, message.toString()).disableWebPagePreview(false));
        }
    }
}
