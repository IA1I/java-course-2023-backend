package edu.java.bot.client.scrapper;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.exception.ServerException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class DefaultChatClient implements ChatClient {
    private static final String URI_TG_CHAT = "/tg-chat/{chatId}";
    private static final String MESSAGE = "Server error";
    private final WebClient webClient;
    private final Retry retry;

    public DefaultChatClient(ApplicationConfig applicationConfig, Retry retry) {
        this(applicationConfig.scrapperBaseUrl(), retry);
    }

    public DefaultChatClient(String baseUrl, Retry retry) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.retry = retry;
    }

    @Override
    public Mono<String> registerChat(long chatId) {
        return webClient
            .post()
            .uri(URI_TG_CHAT, chatId)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(String.class)
            .retryWhen(retry);
    }

    @Override
    public Mono<String> deleteChat(long chatId) {
        return webClient
            .delete()
            .uri(URI_TG_CHAT, chatId)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(String.class)
            .retryWhen(retry);
    }
}
