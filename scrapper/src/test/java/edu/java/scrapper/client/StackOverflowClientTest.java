package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.client.stackoverflow.DefaultStackOverflowClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dto.QuestionResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.dto.QuestionResponse.Item;

public class StackOverflowClientTest {
    static WireMockServer wireMockServer;
    static StackOverflowClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new DefaultStackOverflowClient(wireMockServer.baseUrl());
    }

    @Test
    void shouldReturnQuestionActivity() {
        Item expected = new Item(
            true,
            1004342,
            26,
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC),
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(1680185464L), ZoneOffset.UTC)
        );

        String json = readFile("src/test/resources/stackoverflow/response.json");
        wireMockServer.stubFor(get("/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<QuestionResponse> response = client.getQuestionActivity("1642028");
        var res = response.block();
        System.out.println(res);
        Item actual = response.block().getItem();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnNullForEmptyJson() {
        String json = readFile("src/test/resources/stackoverflow/empty_response.json");
        wireMockServer.stubFor(get("/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<QuestionResponse> response = client.getQuestionActivity("1642028");
        Item actual = response.block().getItem();

        Assertions.assertThat(actual).isNull();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    private String readFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                return Files.readString(Paths.get("scrapper/" + fileName));
            }
            return Files.readString(path);
        } catch (IOException e) {
            return "{}";
        }
    }
}
