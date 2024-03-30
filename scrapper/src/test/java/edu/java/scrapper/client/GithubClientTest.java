package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.client.github.DefaultGithubClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.dto.response.UpdateResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.TestUtils.readFile;

public class GithubClientTest {

    static WireMockServer wireMockServer;
    static GithubClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new DefaultGithubClient(wireMockServer.baseUrl(), "token");
    }

    @Test
    void shouldReturnLastUpdate() {
        UpdateResponse expected =
            new UpdateResponse(17230697424L, OffsetDateTime.parse("2024-02-23T12:00:13Z"), "push");

        String json = readFile("src/test/resources/github/response.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<List<UpdateResponse>> response = client.getRepositoryActivity("IA1I", "java-course-2023-backend");
        UpdateResponse actual = response.block().getFirst();

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

        Mono<List<UpdateResponse>> response = client.getRepositoryActivity("IA1I", "java-course-2023-backend");
        List<UpdateResponse> actual = response.block();

        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

}
