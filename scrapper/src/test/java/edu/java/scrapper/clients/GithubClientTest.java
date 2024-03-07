package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.clients.github.DefaultGithubClient;
import edu.java.scrapper.clients.github.GithubClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.scrapper.dto.Update;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;


public class GithubClientTest {

    static WireMockServer wireMockServer;
    static GithubClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new DefaultGithubClient(wireMockServer.baseUrl());
    }

    @Test
    void shouldReturnLastUpdate() {
        Update expected = new Update(17230697424L, OffsetDateTime.parse("2024-02-23T12:00:13Z"));

        String json = readFile("src/test/resources/github/response.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<List<Update>> response = client.getRepositoryActivity("IA1I", "java-course-2023-backend");
        Update actual = response.block().getFirst();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyListForEmptyJson() {
        String json = readFile("src/test/resources/github/empty_response.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<List<Update>> response = client.getRepositoryActivity("IA1I", "java-course-2023-backend");
        List<Update> actual = response.block();

        Assertions.assertThat(actual.isEmpty()).isTrue();
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
            return "[]";
        }
    }
}
