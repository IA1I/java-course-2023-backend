package edu.java.bot.client.scrapper;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import reactor.core.publisher.Mono;

public interface LinkClient {
    Mono<ListLinksResponse> getAllLinks(long chatId);

    Mono<LinkResponse> trackLink(long chatId, AddLinkRequest requestBody);

    Mono<LinkResponse> untrackLink(long chatId, RemoveLinkRequest requestBody);
}
