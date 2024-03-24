package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.LinkClient;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class ListCommand extends AbstractCommand {
    private final LinkClient linkClient;

    @Autowired
    public ListCommand(UserMessageProcessor processor, LinkClient linkClient) {
        super(processor);
        this.linkClient = linkClient;
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
        Mono<ListLinksResponse> responseMono = linkClient.getAllLinks(chatId);
        ListLinksResponse links = responseMono.block();
        StringBuilder stringBuilder = new StringBuilder();
        for (LinkResponse link : links.links()) {
            stringBuilder.append(link.url())
                .append(" ")
                .append("updated")
                .append(LINE_SEPARATOR);
        }

        return new SendMessage(chatId, stringBuilder.toString());
    }

}
