package edu.java.scrapper.client.stackoverflow;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.response.AnswerResponse;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
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

    @Override
    public Mono<CommentResponse> getQuestionComments(String id) {
        return webClient
            .get()
            .uri("/questions/{id}/comments?site=stackoverflow", id)
            .retrieve()
            .bodyToMono(CommentResponse.class);
    }

    @Override
    public Mono<AnswerResponse> getQuestionAnswers(String id) {
        return webClient
            .get()
            .uri("/questions/{id}/answers?site=stackoverflow", id)
            .retrieve()
            .bodyToMono(AnswerResponse.class);
    }
}
