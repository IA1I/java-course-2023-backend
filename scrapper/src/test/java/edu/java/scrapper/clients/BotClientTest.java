package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.DefaultBotClient;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class BotClientTest {
    static WireMockServer wireMockServer;
    static BotClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        client = new DefaultBotClient(wireMockServer.baseUrl());
    }

    @Test
    void shouldReturnResponseForLinkUpdateRequest() {
        String expected = "Updates received";
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(0, null, null, null);

        wireMockServer.stubFor(post("/updates")
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("Updates received")
            )
        );

        Mono<String> response = client.sendUpdate(linkUpdateRequest);
        String actual = response.block();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }
}
