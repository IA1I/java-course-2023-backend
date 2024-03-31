package edu.java.scrapper.service.jdbc;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.Question;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.TestUtils.readFile;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.access-type=jdbc")
public class JdbcLinkServiceTest extends IntegrationTest {
    @Autowired
    private ChatService chatService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcQuestionRepository questionRepository;

    static WireMockServer wireMockServer;

    @BeforeAll
    public static void beforeAll(@Value("${wire-mock.port}") int port) {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowChatIsNotRegisteredExceptionForAdd() {
        Assertions.assertThrows(
            ChatIsNotRegisteredException.class,
            () -> linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );

    }

    @Test
    @Transactional
    @Rollback
    void shouldAddLink() throws URISyntaxException {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        List<Link> links = linkService.listAll(1L);
        Link actual = links.getFirst();

        assertThat(actual.getUri()).isEqualTo(expected.getUri());
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowReAddLinkExceptionForAdd() throws URISyntaxException {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        Assertions.assertThrows(
            ReAddLinkException.class,
            () -> linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowChatIsNotRegisteredExceptionForDelete() {
        Assertions.assertThrows(
            ChatIsNotRegisteredException.class,
            () -> linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowLinkIsNotTrackedExceptionForDelete() {
        chatService.register(1L);

        Assertions.assertThrows(
            LinkIsNotTrackedException.class,
            () -> linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldUntrackLinkButNotDelete() throws URISyntaxException {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        List<Link> actual = linkService.listAll(1L);

        assertThat(actual).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldUntrackLinkAndDelete() throws URISyntaxException {
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

        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/tinkoff_edu2023"));

        linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        List<Link> actual = linkService.listAll(1L);

        assertThat(actual).isEmpty();
        Assertions.assertThrows(
            LinkIsNotTrackedException.class,
            () -> linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowChatIsNotRegisteredExceptionForListALl() {
        Assertions.assertThrows(ChatIsNotRegisteredException.class, () -> linkService.listAll(1L));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnAllTrackedLinksByChat() throws URISyntaxException {
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
        json = readFile("src/test/resources/links-updater/github_repo_sanyarnd.json");
        wireMockServer.stubFor(get("/repos/sanyarnd/java-course-2023-backend-template/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        List<URI> expected = List.of(
            new URI("https://github.com/IA1I/java-course-2023-backend"),
            new URI("https://github.com/IA1I/tinkoff_edu2023"),
            new URI("https://github.com/sanyarnd/java-course-2023-backend-template")
        );

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        linkService.add(1L, new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));

        List<Link> actual = linkService.listAll(1L);

        assertThat(actual).extracting(Link::getUri).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldSaveLinkAndQuestion() throws URISyntaxException {
        Question expected = new Question();
        expected.setCommentsCount(0);
        expected.setAnswersCount(26);

        String json = readFile("src/test/resources/stackoverflow/response.json");
        wireMockServer.stubFor(get("/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/stackoverflow/comments_response_1642028.json");
        wireMockServer.stubFor(get("/questions/1642028/comments?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        chatService.register(1L);
        linkService.add(1L, new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        Link link =
            linkRepository.getByURI(new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        expected.setLinkId(link.getLinkId());

        Question actual = questionRepository.get(link.getLinkId());

        assertThat(actual).isEqualTo(expected);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }
}
