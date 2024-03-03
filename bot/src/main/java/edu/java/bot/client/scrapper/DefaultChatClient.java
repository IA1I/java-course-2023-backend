package edu.java.bot.client.scrapper;

import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DefaultChatClient implements ChatClient {
    private static final String URI_TG_CHAT = "/tg-chat/{chatId}";
    private final WebClient webClient;

    public DefaultChatClient(ApplicationConfig applicationConfig) {
        this(applicationConfig.scrapperBaseUrl());
    }

    public DefaultChatClient(String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Mono<String> registerChat(long chatId) {
        return webClient
            .post()
            .uri(URI_TG_CHAT, chatId)
            .retrieve()
            .bodyToMono(String.class);
    }

    @Override
    public Mono<String> deleteChat(long chatId) {
        return webClient
            .delete()
            .uri(URI_TG_CHAT, chatId)
            .retrieve()
            .bodyToMono(String.class);
    }
}
