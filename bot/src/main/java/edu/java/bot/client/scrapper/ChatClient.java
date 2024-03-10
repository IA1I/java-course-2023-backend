package edu.java.bot.client.scrapper;

import reactor.core.publisher.Mono;

public interface ChatClient {
    Mono<String> registerChat(long chatId);

    Mono<String> deleteChat(long chatId);
}
