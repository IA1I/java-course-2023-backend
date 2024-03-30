package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.LinkClient;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class UntrackCommand extends AbstractCommand {
    private final LinkClient linkClient;

    @Autowired
    public UntrackCommand(UserMessageProcessor processor, LinkClient linkClient) {
        super(processor);
        this.linkClient = linkClient;
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

        return new SendMessage(chatId, getText(update)).disableWebPagePreview(true);
    }

    private String getText(Update update) {
        long id = update.message().from().id();
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

            Mono<LinkResponse> linkResponseMono = linkClient.untrackLink(id, new RemoveLinkRequest(links[i]));
            LinkResponse response = linkResponseMono.block();
            text.append("untracked");
            text.append(LINE_SEPARATOR);
        }

        return text.toString();
    }
}
