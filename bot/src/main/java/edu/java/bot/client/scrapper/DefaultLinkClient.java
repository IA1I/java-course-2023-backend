package edu.java.bot.client.scrapper;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DefaultLinkClient implements LinkClient {
    private static final String URI_LINKS = "/links";
    public static final String HEADER_TG_CHAT_ID = "Tg-Chat-Id";
    private final WebClient webClient;

    public DefaultLinkClient(ApplicationConfig applicationConfig) {
        this(applicationConfig.scrapperBaseUrl());
    }

    public DefaultLinkClient(String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Mono<ListLinksResponse> getAllLinks(long chatId) {
        return webClient
            .get()
            .uri(URI_LINKS)
            .header(HEADER_TG_CHAT_ID, String.valueOf(chatId))
            .retrieve()
            .bodyToMono(ListLinksResponse.class);
    }

    @Override
    public Mono<LinkResponse> trackLink(long chatId, AddLinkRequest requestBody) {
        return webClient
            .post()
            .uri(URI_LINKS)
            .header(HEADER_TG_CHAT_ID, String.valueOf(chatId))
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }

    @Override
    public Mono<LinkResponse> untrackLink(long chatId, RemoveLinkRequest requestBody) {
        return webClient
            .method(HttpMethod.DELETE)
            .uri(URI_LINKS)
            .header(HEADER_TG_CHAT_ID, String.valueOf(chatId))
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(LinkResponse.class);
    }
}
