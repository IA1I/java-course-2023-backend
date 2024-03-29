package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.QuestionResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DefaultStackOverflowClient implements StackOverflowClient {
    private final WebClient webClient;

    public DefaultStackOverflowClient(ApplicationConfig applicationConfig) {
        this(applicationConfig.stackOverflowBaseUrl());
    }

    public DefaultStackOverflowClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Mono<QuestionResponse> getQuestionActivity(String id) {
        return webClient
            .get()
            .uri("/questions/{id}?site=stackoverflow", id)
            .retrieve()
            .bodyToMono(QuestionResponse.class);
    }
}
