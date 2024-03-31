package edu.java.scrapper.client.stackoverflow;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.response.AnswerResponse;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.exception.ServerException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class DefaultStackOverflowClient implements StackOverflowClient {
    private static final String MESSAGE = "Server error";
    private final WebClient webClient;
    private final Retry retry;

    public DefaultStackOverflowClient(ApplicationConfig applicationConfig, Retry retry) {
        this(applicationConfig.stackOverflowBaseUrl(), retry);
    }

    public DefaultStackOverflowClient(String baseUrl, Retry retry) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.retry = retry;
    }

    @Override
    public Mono<QuestionResponse> getQuestionActivity(String id) {
        return webClient
            .get()
            .uri("/questions/{id}?site=stackoverflow", id)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(QuestionResponse.class)
            .retryWhen(retry);
    }

    @Override
    public Mono<CommentResponse> getQuestionComments(String id) {
        return webClient
            .get()
            .uri("/questions/{id}/comments?site=stackoverflow", id)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(CommentResponse.class)
            .retryWhen(retry);
    }

    @Override
    public Mono<AnswerResponse> getQuestionAnswers(String id) {
        return webClient
            .get()
            .uri("/questions/{id}/answers?site=stackoverflow", id)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(AnswerResponse.class)
            .retryWhen(retry);
    }
}
