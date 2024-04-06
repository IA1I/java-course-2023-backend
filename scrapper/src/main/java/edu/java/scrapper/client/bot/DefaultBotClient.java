package edu.java.scrapper.client.bot;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import edu.java.scrapper.exception.ServerException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class DefaultBotClient implements BotClient {
    private static final String MESSAGE = "Server error";
    private final WebClient webClient;
    private final Retry retry;

    public DefaultBotClient(ApplicationConfig applicationConfig, Retry retry) {
        this(applicationConfig.botBaseUrl(), retry);
    }

    public DefaultBotClient(String baseUrl, Retry retry) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.retry = retry;
    }

    @Override
    public Mono<String> sendUpdate(LinkUpdateRequest request) {
        return webClient
            .post()
            .uri("/updates")
            .bodyValue(request)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(String.class)
            .retryWhen(retry);
    }
}
