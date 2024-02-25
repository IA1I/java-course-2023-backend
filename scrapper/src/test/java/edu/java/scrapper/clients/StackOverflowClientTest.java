package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.clients.stackoverflow.DefaultStackOverflowClient;
import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dto.QuestionResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

        String json = readFile("scrapper/src/test/resources/stackoverflow/response.json");
        wireMockServer.stubFor(get("/2.3/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        QuestionResponse response = client.getQuestionActivity("1642028");
        Item actual = response.getItem();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnNullForEmptyJson() {
        String json = readFile("scrapper/src/test/resources/stackoverflow/empty_response.json");
        wireMockServer.stubFor(get("/2.3/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        QuestionResponse response = client.getQuestionActivity("1642028");
        Item actual = response.getItem();

        Assertions.assertThat(actual).isNull();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    private String readFile(String fileName) {
        try {
            return Files.readString(Path.of(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);

//            return "{}";
        }
    }
}
