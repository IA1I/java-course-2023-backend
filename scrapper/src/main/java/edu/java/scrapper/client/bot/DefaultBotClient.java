package edu.java.scrapper.client.bot;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DefaultBotClient implements BotClient {
    private final WebClient webClient;

    public DefaultBotClient(ApplicationConfig applicationConfig) {
        this(applicationConfig.botBaseUrl());
    }

    public DefaultBotClient(String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Mono<String> sendUpdate(LinkUpdateRequest request) {
        return webClient
            .post()
            .uri("/updates")
            .retrieve()
            .bodyToMono(String.class);
    }
}
