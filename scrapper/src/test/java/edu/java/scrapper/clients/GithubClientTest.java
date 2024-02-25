package edu.java.scrapper.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.clients.github.DefaultGithubClient;
import edu.java.scrapper.clients.github.GithubClient;
import edu.java.scrapper.dto.RepositoryResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.dto.RepositoryResponse.*;

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

        RepositoryResponse response = client.getRepositoryActivity("IA1I", "java-course-2023-backend");
        Update actual = response.getLastUpdate();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnNullForEmptyJson() {
        String json = readFile("resources/github/empty_response.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        RepositoryResponse response = client.getRepositoryActivity("IA1I", "java-course-2023-backend");
        Update actual = response.getLastUpdate();

        Assertions.assertThat(actual).isNull();
    }

    @Test
    void shouldReturnNullForBadResponse() {
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("json")
            )
        );

        RepositoryResponse response = client.getRepositoryActivity("IA1I", "java-course-2023-backend");
        Update actual = response.getLastUpdate();

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
//            return "[]";
        }
    }
}
