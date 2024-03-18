package edu.java.scrapper.service.jooq;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkUpdater;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.TestUtils.readFile;

@SpringBootTest
public class JooqLinksUpdaterServiceTest extends IntegrationTest {
    @Autowired
    @Qualifier("jooqLinksUpdaterService")
    private LinkUpdater linkUpdater;
    @Autowired
    @Qualifier("jooqLinkService")
    private LinkService linkService;
    @Autowired
    @Qualifier("jooqChatService")
    private ChatService chatService;

    static WireMockServer wireMockServer;

    @BeforeAll
    public static void beforeAll(@Value("${wire-mock.port}") int port) {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateZeroLinksWithoutLinks() {
        int updatesCount = linkUpdater.update();

        Assertions.assertThat(updatesCount).isZero();
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateZeroLinks() throws URISyntaxException {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/github_repo_tinkoff.json");
        wireMockServer.stubFor(get("/repos/IA1I/tinkoff_edu2023/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/stackoverflow_question.json");
        wireMockServer.stubFor(get("/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/stackoverflow_comments.json");
        wireMockServer.stubFor(get("/questions/1642028/comments?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        linkService.add(1L, new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        int updatesCount = linkUpdater.update();

        Assertions.assertThat(updatesCount).isZero();
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateLinks() throws URISyntaxException {
        String json = readFile("src/test/resources/links-updater/updated_github_repo_backend.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/updated_github_repo_tinkoff.json");
        wireMockServer.stubFor(get("/repos/IA1I/tinkoff_edu2023/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/updated_stackoverflow_question.json");
        wireMockServer.stubFor(get("/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/stackoverflow_comments.json");
        wireMockServer.stubFor(get("/questions/1642028/comments?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        linkService.add(1L, new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        int updatesCount = linkUpdater.update();

        Assertions.assertThat(updatesCount).isEqualTo(3);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

}
