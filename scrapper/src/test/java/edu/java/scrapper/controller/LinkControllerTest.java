package edu.java.scrapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcTrackedLinkRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.request.AddLinkRequest;
import edu.java.scrapper.dto.request.RemoveLinkRequest;
import edu.java.scrapper.dto.response.ApiErrorResponse;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static edu.java.scrapper.TestUtils.readFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.access-type=jdbc")
@AutoConfigureMockMvc
public class LinkControllerTest extends IntegrationTest {
    @Autowired
    private JdbcChatRepository chatRepository;
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcTrackedLinkRepository trackedLinkRepository;
    @Autowired
    private LinkService linkService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    static WireMockServer wireMockServer;

    @BeforeAll
    public static void beforeAll(@Value("${wire-mock.port}") int port) {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyListLinksResponseForChatWithoutLinks() throws Exception {
        Long tgChatId = 1L;
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        mockMvc.perform(
                get("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(0));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForNotRegisteredChatForGetLinks() throws Exception {
        ApiErrorResponse expected = new ApiErrorResponse(
            "Chat is not registered",
            "409 CONFLICT",
            "edu.java.scrapper.exception.ChatIsNotRegisteredException",
            "Chat is not registered",
            null
        );
        Long tgChatId = 2L;

        mockMvc.perform(
                get("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value(expected.description()))
            .andExpect(jsonPath("$.code").value(expected.code()))
            .andExpect(jsonPath("$.exception_name").value(expected.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(expected.exceptionMessage()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnTrackedLinksForRegisteredChat() throws Exception {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(WireMock.get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/github_repo_tinkoff.json");
        wireMockServer.stubFor(WireMock.get("/repos/IA1I/tinkoff_edu2023/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/github_repo_sanyarnd.json");
        wireMockServer.stubFor(WireMock.get("/repos/sanyarnd/java-course-2023-backend-template/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        Link link1 = new Link();
        link1.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        Link link2 = new Link();
        link2.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        Link link3 = new Link();
        link3.setUri(new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        Long tgChatId = 3L;
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        linkService.add(tgChatId, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(tgChatId, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        linkService.add(tgChatId, new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));

        mockMvc.perform(
                get("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(3))
            .andExpect(jsonPath("$.links[0].url").value("https://github.com/IA1I/java-course-2023-backend"))
            .andExpect(jsonPath("$.links[1].url").value("https://github.com/IA1I/tinkoff_edu2023"))
            .andExpect(jsonPath("$.links[2].url").value("https://github.com/sanyarnd/java-course-2023-backend-template"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForNotRegisteredChatForPostLinks() throws Exception {
        ApiErrorResponse expected = new ApiErrorResponse(
            "Chat is not registered",
            "409 CONFLICT",
            "edu.java.scrapper.exception.ChatIsNotRegisteredException",
            "Chat is not registered",
            null
        );
        Long tgChatId = 4L;
        AddLinkRequest request = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        mockMvc.perform(
                post("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value(expected.description()))
            .andExpect(jsonPath("$.code").value(expected.code()))
            .andExpect(jsonPath("$.exception_name").value(expected.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(expected.exceptionMessage()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForReAddingLinkForPostLinks() throws Exception {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(WireMock.get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        ApiErrorResponse expected = new ApiErrorResponse(
            "Re-adding link",
            "409 CONFLICT",
            "edu.java.scrapper.exception.ReAddLinkException",
            "Link is already being tracked",
            null
        );
        Long tgChatId = 5L;
        AddLinkRequest request = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        linkService.add(tgChatId, new URI("https://github.com/IA1I/java-course-2023-backend"));
        mockMvc.perform(
                post("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value(expected.description()))
            .andExpect(jsonPath("$.code").value(expected.code()))
            .andExpect(jsonPath("$.exception_name").value(expected.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(expected.exceptionMessage()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldAddGithubLink() throws Exception {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(WireMock.get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        Long tgChatId = 6L;
        AddLinkRequest request = new AddLinkRequest("https://github.com/IA1I/java-course-2023-backend");
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        mockMvc.perform(
                post("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://github.com/IA1I/java-course-2023-backend"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldAddStackOverFlowLink() throws Exception {
        String json = readFile("src/test/resources/links-updater/updated_stackoverflow_question.json");
        wireMockServer.stubFor(WireMock.get("/questions/1642028?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        json = readFile("src/test/resources/links-updater/stackoverflow_comments.json");
        wireMockServer.stubFor(WireMock.get("/questions/1642028/comments?site=stackoverflow")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );
        Long tgChatId = 7L;
        AddLinkRequest request = new AddLinkRequest("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c");
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        mockMvc.perform(
                post("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForNotRegisteredChatForDeleteLinks() throws Exception {
        ApiErrorResponse expected = new ApiErrorResponse(
            "Chat is not registered",
            "409 CONFLICT",
            "edu.java.scrapper.exception.ChatIsNotRegisteredException",
            "Chat is not registered",
            null
        );
        Long tgChatId = 8L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://github.com/IA1I/java-course-2023-backend");

        mockMvc.perform(
                delete("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value(expected.description()))
            .andExpect(jsonPath("$.code").value(expected.code()))
            .andExpect(jsonPath("$.exception_name").value(expected.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(expected.exceptionMessage()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForNotTrackedLinkForDeleteLinks() throws Exception {
        ApiErrorResponse expected = new ApiErrorResponse(
            "Link is not tracked",
            "409 CONFLICT",
            "edu.java.scrapper.exception.LinkIsNotTrackedException",
            "Link is not tracked",
            null
        );
        Long tgChatId = 9L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://github.com/IA1I/java-course-2023-backend");
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        mockMvc.perform(
                delete("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value(expected.description()))
            .andExpect(jsonPath("$.code").value(expected.code()))
            .andExpect(jsonPath("$.exception_name").value(expected.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(expected.exceptionMessage()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteLink() throws Exception {
        String json = readFile("src/test/resources/links-updater/github_repo_backend.json");
        wireMockServer.stubFor(WireMock.get("/repos/IA1I/java-course-2023-backend/activity")
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            )
        );

        Long tgChatId = 10L;
        RemoveLinkRequest request = new RemoveLinkRequest("https://github.com/IA1I/java-course-2023-backend");
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        linkService.add(tgChatId, new URI("https://github.com/IA1I/java-course-2023-backend"));
        mockMvc.perform(
                delete("/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Tg-Chat-Id", tgChatId)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://github.com/IA1I/java-course-2023-backend"));
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }
}
