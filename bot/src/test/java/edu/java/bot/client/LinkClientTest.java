package edu.java.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.bot.client.scrapper.DefaultLinkClient;
import edu.java.bot.client.scrapper.LinkClient;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class LinkClientTest {
    static WireMockServer wireMockServer;
    static LinkClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new DefaultLinkClient(wireMockServer.baseUrl());
    }

    @Test
    void shouldReturnListLinksResponseForRegisteredChat() {
        ListLinksResponse expected = new ListLinksResponse(new ArrayList<>(), 0);

        String json = readFile("src/test/resources/scrapper/list-links-response.json");
        wireMockServer.stubFor(get("/links")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withHeader("Tg-Chat-Id", "1")
                    .withBody(json)
            )
        );

        Mono<ListLinksResponse> response = client.getAllLinks(1);
        ListLinksResponse actual = response.block();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnLinksResponseForRegisteredChat() {
        LinkResponse expected = new LinkResponse(0L, "https://github.com/IA1I/java-course-2023-backend");
        AddLinkRequest addLinkRequest = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        String json = readFile("src/test/resources/scrapper/add-link-response.json");
        wireMockServer.stubFor(post("/links")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withHeader("Tg-Chat-Id", "2")
                    .withBody(json)
            )
        );

        Mono<LinkResponse> response = client.trackLink(2, addLinkRequest);
        LinkResponse actual = response.block();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnLinksResponseForRegisteredChatForRemove() {
        LinkResponse expected = new LinkResponse(0L, "https://github.com/IA1I/java-course-2023-backend");
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        String json = readFile("src/test/resources/scrapper/remove-link-response.json");
        wireMockServer.stubFor(delete("/links")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withHeader("Tg-Chat-Id", "3")
                    .withBody(json)
            )
        );

        Mono<LinkResponse> response = client.untrackLink(3, removeLinkRequest);
        LinkResponse actual = response.block();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    private String readFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                return Files.readString(Paths.get("bot/" + fileName));
            }
            return Files.readString(path);
        } catch (IOException e) {
            return "[]";
        }
    }
}
