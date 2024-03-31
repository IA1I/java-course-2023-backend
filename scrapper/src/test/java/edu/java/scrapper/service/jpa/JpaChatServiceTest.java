package edu.java.scrapper.service.jpa;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.entity.ChatEntity;
import edu.java.scrapper.dao.entity.LinkEntity;
import edu.java.scrapper.dao.entity.QuestionEntity;
import edu.java.scrapper.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.scrapper.dao.repository.jpa.JpaChatRepository;
import edu.java.scrapper.dao.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.dao.repository.jpa.JpaQuestionRepository;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.ReRegistrationException;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static edu.java.scrapper.TestUtils.readFile;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "app.access-type=jpa")
public class JpaChatServiceTest extends IntegrationTest {
    @Autowired
    private ChatService chatService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private JpaChatRepository chatRepository;
    @Autowired
    private JpaLinkRepository linkRepository;
    @Autowired
    private JpaQuestionRepository questionRepository;

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
        ChatEntity expected = new ChatEntity();
        expected.setTgChatId(1L);

        chatService.register(1L);

        ChatEntity actual = chatRepository.findByTgChatId(1L).orElse(null);

        Assertions.assertThat(actual.getTgChatId()).isEqualTo(expected.getTgChatId());
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
        List<ChatEntity> expected = List.of(new ChatEntity());
        expected.getFirst().setTgChatId(2L);

        chatService.register(1L);
        chatService.register(2L);
        chatService.unregister(1L);

        List<ChatEntity> actual = chatRepository.findAll();

        Assertions.assertThat(actual.getFirst().getTgChatId()).isEqualTo(expected.getFirst().getTgChatId());
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

        List<LinkEntity> actual = linkRepository.findAll();

        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteUntrackedLinksAfterDeletingChat() throws URISyntaxException {
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

        List<String> expected = List.of(
            "https://github.com/IA1I/java-course-2023-backend"
        );
        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        chatService.unregister(1L);

        List<LinkEntity> actual = linkRepository.findAll();

        Assertions.assertThat(actual).extracting(LinkEntity::getUri).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteUntrackedLinksAndQuestions() throws URISyntaxException {
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
        json = readFile("src/test/resources/stackoverflow/response.json");
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

        List<String> expected = List.of(
            "https://github.com/IA1I/java-course-2023-backend"
        );
        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        linkService.add(1L, new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        LinkEntity link =
            linkRepository.findByUri("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c").get();
        chatService.unregister(1L);

        List<LinkEntity> actualLinks = linkRepository.findAll();

        Assertions.assertThat(actualLinks).extracting(LinkEntity::getUri).isEqualTo(expected);
        Optional<QuestionEntity> question = questionRepository.findById(link.getLinkId());
        Assertions.assertThat(question).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowChatIsNotRegisteredExceptionForDeletingUnregisterChat() {
        assertThrows(ChatIsNotRegisteredException.class, () -> chatService.unregister(1L));
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }
}
