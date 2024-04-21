package edu.java.scrapper.service.http;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import edu.java.scrapper.service.MessageSender;
import reactor.core.publisher.Mono;

public class BotClientSender implements MessageSender {
    private final BotClient botClient;

    public BotClientSender(BotClient botClient) {
        this.botClient = botClient;
    }

    @Override
    public void send(LinkUpdateRequest update) {
        Mono<String> sentUpdate = botClient.sendUpdate(update);
        sentUpdate.block();
    }
}
