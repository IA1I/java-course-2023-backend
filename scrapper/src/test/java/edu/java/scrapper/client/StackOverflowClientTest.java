package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.client.stackoverflow.DefaultStackOverflowClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dto.response.AnswerResponse;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.TestUtils.readFile;
import static edu.java.scrapper.dto.response.QuestionResponse.Item;

public class StackOverflowClientTest {
    static WireMockServer wireMockServer;
    static StackOverflowClient client;

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new DefaultStackOverflowClient(wireMockServer.baseUrl(), Retry.max(1L));
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

    @Test
    void shouldReturnQuestionCommentsAndCommentsCountIsZero() {
        String json = readFile("src/test/resources/stackoverflow/comments_response_1642028.json");
        wireMockServer.stubFor(get("/questions/1642028/comments?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<CommentResponse> response = client.getQuestionComments("1642028");
        CommentResponse commentResponse = response.block();

        int actual = commentResponse.getCommentsCount();

        Assertions.assertThat(actual).isZero();
    }

    @Test
    void shouldReturnQuestionCommentsAndCommentsCountIsEqualTo2() {
        int expected = 2;

        String json = readFile("src/test/resources/stackoverflow/comments_response_75867719.json");
        wireMockServer.stubFor(get("/questions/75867719/comments?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<CommentResponse> response = client.getQuestionComments("75867719");

        CommentResponse commentResponse = response.block();

        int actual = commentResponse.getCommentsCount();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnQuestionAnswersAndAnswersCountIsEqualTo26() {
        int expected = 26;

        String json = readFile("src/test/resources/stackoverflow/answers_response_1642028.json");
        wireMockServer.stubFor(get("/questions/1642028/answers?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<AnswerResponse> response = client.getQuestionAnswers("1642028");

        AnswerResponse answerResponse = response.block();

        int actual = answerResponse.getAnswersCount();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnQuestionAnswersAndAnswersCountIsZero() {
        String json = readFile("src/test/resources/stackoverflow/answers_response_75867719.json");
        wireMockServer.stubFor(get("/questions/75867719/answers?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Mono<AnswerResponse> response = client.getQuestionAnswers("75867719");

        AnswerResponse answerResponse = response.block();

        int actual = answerResponse.getAnswersCount();

        Assertions.assertThat(actual).isZero();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

}
