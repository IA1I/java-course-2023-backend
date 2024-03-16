package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.repository.JdbcLinkDao;
import edu.java.scrapper.dao.repository.JdbcTrackedLinkDao;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.Update;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.service.LinkUpdater;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class JdbcLinksUpdaterService implements LinkUpdater {

    private static final String SEPARATOR = "/";
    private static final String DESCRIPTION = "Link updated";
    private static final int QUESTION_ID_INDEX = 2;
    private static final long CHECK_TIME = 3L;
    private static final int REPOSITORY_OWNER_INDEX = 1;
    private static final int REPOSITORY_NAME_INDEX = 2;
    private final JdbcLinkDao jdbcLinkDao;
    private final JdbcTrackedLinkDao trackedLinkDao;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Autowired
    public JdbcLinksUpdaterService(
        JdbcLinkDao jdbcLinkDao,
        JdbcTrackedLinkDao trackedLinkDao,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        BotClient botClient
    ) {
        this.jdbcLinkDao = jdbcLinkDao;
        this.trackedLinkDao = trackedLinkDao;
        this.githubClient = githubClient;
        this.stackOverflowClient = stackOverflowClient;
        this.botClient = botClient;
    }

    @Override
    public int update() {
        int updatesCount = 0;
//        List<Link> links = getLinksToCheck();
        List<Link> links = jdbcLinkDao.getLinksToCheck();
        log.info("Get links {}", links);

        for (Link link : links) {
            OffsetDateTime updatedAt = getUpdateTime(link);

            if (updatedAt.isAfter(link.getLastCheck())) {
                LinkUpdateRequest linkUpdateRequest = getLinkUpdateRequest(link);
                botClient.sendUpdate(linkUpdateRequest);
                updatesCount++;

                log.info("Send update {} to bot", linkUpdateRequest);
            }
        }

        return updatesCount;
    }

    private OffsetDateTime getUpdateTime(Link link) {
        URI uri = link.getUri();
        OffsetDateTime updatedAt = OffsetDateTime.now();

        if (uri.getHost().equals("github.com")) {
            updatedAt = getTimeOfUpdate(uri);
        } else if (uri.getHost().equals("stackoverflow.com")) {
            updatedAt = getLastActivityDate(uri);
        }

        return updatedAt;
    }

    private LinkUpdateRequest getLinkUpdateRequest(Link link) {
        long id = link.getLinkId();
        String url = link.getUri().toString();
        Long[] tgChatIds = getTgChatsIdForUpdate(link);

        return new LinkUpdateRequest(id, url, DESCRIPTION, tgChatIds);
    }

    private OffsetDateTime getLastActivityDate(URI uri) {
        String[] path = uri.getPath().split(SEPARATOR);
        String questionId = path[QUESTION_ID_INDEX];

        Mono<QuestionResponse> questionActivity = stackOverflowClient.getQuestionActivity(questionId);
        QuestionResponse response = questionActivity.block();

        log.info("Get question response {}", response);
        return response.getItem().lastActivityDate();
    }

    private Long[] getTgChatsIdForUpdate(Link link) {
        List<Chat> chats = trackedLinkDao.getAllChatsByLinkId(link.getLinkId());
        Long[] tgChatIds = new Long[chats.size()];
        for (int i = 0; i < chats.size(); i++) {
            tgChatIds[i] = chats.get(i).getTgChatId();
        }

        return tgChatIds;
    }

    private OffsetDateTime getTimeOfUpdate(URI uri) {
        String[] path = uri.getPath().split(SEPARATOR);
        String owner = path[REPOSITORY_OWNER_INDEX];
        String repo = path[REPOSITORY_NAME_INDEX];

        Mono<List<Update>> repositoryActivity = githubClient.getRepositoryActivity(owner, repo);
        Update update = repositoryActivity.block().getFirst();
        log.info("Get github update {}", update);
        return update.timestamp();
    }

    private List<Link> getLinksToCheck() {
        OffsetDateTime now = OffsetDateTime.now();
        Predicate<Link> filter = link -> Duration.between(link.getLastCheck(), now).toHours() > CHECK_TIME;

        return jdbcLinkDao.getAll().stream()
            .filter(filter)
            .toList();
    }
}
