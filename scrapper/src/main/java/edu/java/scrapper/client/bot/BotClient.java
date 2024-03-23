package edu.java.scrapper.client.bot;

import edu.java.scrapper.dto.request.LinkUpdateRequest;
import reactor.core.publisher.Mono;

public interface BotClient {

    Mono<String> sendUpdate(LinkUpdateRequest request);
}
