package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.dto.QuestionResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class DefaultStackOverflowClient implements StackOverflowClient {
    private static final String BASE_URL = "https://api.stackexchange.com";
    private final WebClient webClient;

    public DefaultStackOverflowClient() {
        this(BASE_URL);
    }

    public DefaultStackOverflowClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public QuestionResponse getQuestionActivity(String id) {

        return webClient
            .get()
            .uri("/2.3/questions/{id}?site=stackoverflow", id)
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .block();
    }
}
