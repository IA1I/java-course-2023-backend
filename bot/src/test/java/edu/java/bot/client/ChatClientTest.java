package edu.java.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.bot.client.scrapper.ChatClient;
import edu.java.bot.client.scrapper.DefaultChatClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class ChatClientTest {
    static WireMockServer wireMockServer;
    static ChatClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new DefaultChatClient(wireMockServer.baseUrl(), Retry.max(1L));
    }

    @Test
    void shouldRegisterChat() {
        String expected = "Chat registered";

        wireMockServer.stubFor(post("/tg-chat/1")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("Chat registered")
            )
        );

        Mono<String> response = client.registerChat(1);
        String actual = response.block();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDeleteChat() {
        String expected = "Chat deleted";

        wireMockServer.stubFor(delete("/tg-chat/2")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("Chat deleted")
            )
        );

        Mono<String> response = client.deleteChat(2);
        String actual = response.block();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

}
