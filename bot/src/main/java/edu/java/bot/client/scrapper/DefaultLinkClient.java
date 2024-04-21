package edu.java.bot.client.scrapper;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.exception.ServerException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class DefaultLinkClient implements LinkClient {
    private static final String URI_LINKS = "/links";
    private static final String HEADER_TG_CHAT_ID = "Tg-Chat-Id";
    private static final String MESSAGE = "Server error";
    private final WebClient webClient;
    private final Retry retry;

    public DefaultLinkClient(ApplicationConfig applicationConfig, Retry retry) {
        this(applicationConfig.scrapperBaseUrl(), retry);
    }

    public DefaultLinkClient(String baseUrl, Retry retry) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.retry = retry;
    }

    @Override
    public Mono<ListLinksResponse> getAllLinks(long chatId) {
        return webClient
            .get()
            .uri(URI_LINKS)
            .header(HEADER_TG_CHAT_ID, String.valueOf(chatId))
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(ListLinksResponse.class)
            .retryWhen(retry);
    }

    @Override
    public Mono<LinkResponse> trackLink(long chatId, AddLinkRequest requestBody) {
        return webClient
            .post()
            .uri(URI_LINKS)
            .header(HEADER_TG_CHAT_ID, String.valueOf(chatId))
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(LinkResponse.class)
            .retryWhen(retry);
    }

    @Override
    public Mono<LinkResponse> untrackLink(long chatId, RemoveLinkRequest requestBody) {
        return webClient
            .method(HttpMethod.DELETE)
            .uri(URI_LINKS)
            .header(HEADER_TG_CHAT_ID, String.valueOf(chatId))
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(LinkResponse.class)
            .retryWhen(retry);
    }
}
