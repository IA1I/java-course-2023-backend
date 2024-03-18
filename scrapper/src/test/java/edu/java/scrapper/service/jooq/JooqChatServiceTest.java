package edu.java.scrapper.service.jooq;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.exception.ReRegistrationException;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
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
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JooqChatServiceTest extends IntegrationTest {
    @Autowired
    @Qualifier("jooqChatService")
    private ChatService chatService;
    @Autowired
    @Qualifier("jooqLinkService")
    private LinkService linkService;
    @Autowired
    private JooqChatRepository chatRepository;
    @Autowired
    private JdbcLinkRepository linkRepository;

    static WireMockServer wireMockServer;

    @BeforeAll
    public static void beforeAll(@Value("${wire-mock.port}") int port) {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
    }

    @Test
    @Transactional
    @Rollback
    void shouldSaveChat() {
        Chat expected = new Chat();
        expected.setTgChatId(1L);

        chatService.register(1L);

        Chat actual = chatRepository.getByTgChatId(1L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowReRegistrationException() {
        chatService.register(1L);

        assertThrows(ReRegistrationException.class, () -> chatService.register(1L));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteChat() {
        List<Chat> expected = List.of(new Chat());
        expected.getFirst().setTgChatId(2L);

        chatService.register(1L);
        chatService.register(2L);
        chatService.unregister(1L);

        List<Chat> actual = chatRepository.getAll();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyLinkListIfChatUnregistered() throws URISyntaxException {
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

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        chatService.unregister(1L);

        List<Link> actual = linkRepository.getAll();

        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteUntrackedLinksIfChatUnregistered() throws URISyntaxException {
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

        List<URI> expected = List.of(
            new URI("https://github.com/IA1I/java-course-2023-backend")
        );
        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        chatService.unregister(1L);

        List<Link> actual = linkRepository.getAll();

        Assertions.assertThat(actual).extracting(Link::getUri).isEqualTo(expected);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }
}
