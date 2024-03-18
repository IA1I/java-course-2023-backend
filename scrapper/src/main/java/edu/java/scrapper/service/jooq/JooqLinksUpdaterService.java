package edu.java.scrapper.service.jooq;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.repository.jooq.JooqQuestionRepository;
import edu.java.scrapper.dao.repository.jooq.JooqTrackedLinkRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.Question;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.dto.response.UpdateResponse;
import edu.java.scrapper.dto.update_mapper.UpdateInfo;
import edu.java.scrapper.service.LinkUpdater;
import edu.java.scrapper.service.jdbc.JdbcUtils;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class JooqLinksUpdaterService implements LinkUpdater {
    private static final long CHECK_TIME = 3L;
    private final JooqLinkRepository linkRepository;
    private final JooqTrackedLinkRepository trackedLinkRepository;
    private final JooqQuestionRepository questionRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    public JooqLinksUpdaterService(
        JooqLinkRepository linkRepository,
        JooqTrackedLinkRepository trackedLinkRepository,
        JooqQuestionRepository questionRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        BotClient botClient
    ) {
        this.linkRepository = linkRepository;
        this.trackedLinkRepository = trackedLinkRepository;
        this.questionRepository = questionRepository;
        this.githubClient = githubClient;
        this.stackOverflowClient = stackOverflowClient;
        this.botClient = botClient;
    }

    @Override
    @Transactional
    public int update() {
        int updatesCount = 0;
        List<Link> links = getLinksToCheck();
//        List<Link> links = linkRepository.getLinksToCheck();
        log.info("Get links {}", links);

        for (Link link : links) {
            Question question = null;
            UpdateInfo updateInfo = new UpdateInfo();
            URI uri = link.getUri();

            if (uri.getHost().equals("github.com")) {
                UpdateResponse updateResponse = JdbcUtils.getUpdate(uri, githubClient);
                log.info("Get github update response {}", updateResponse);

                updateInfo.setUpdatedAt(updateResponse.timestamp());
                updateInfo.setDescription(updateResponse.activityType());

                link.setUpdatedAt(updateResponse.timestamp());
            } else if (uri.getHost().equals("stackoverflow.com")) {
                QuestionResponse questionResponse = JdbcUtils.getQuestionResponse(link.getUri(), stackOverflowClient);
                QuestionResponse.Item itemResponse = questionResponse.getItem();
                log.info("Get question response {}", questionResponse);

                CommentResponse commentResponse = JdbcUtils.getCommentResponse(uri, stackOverflowClient);
                log.info("Get comment response {}", commentResponse);
                question = questionRepository.get(link.getLinkId());

                int newAnswers = itemResponse.answerCount() - question.getAnswersCount();
                int newComments = commentResponse.getCommentsCount() - question.getCommentsCount();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("New answers: ")
                    .append(newAnswers)
                    .append("\n")
                    .append("New comments: ")
                    .append(newComments);

                updateInfo.setUpdatedAt(itemResponse.lastActivityDate());
                updateInfo.setDescription(stringBuilder.toString());

                link.setUpdatedAt(itemResponse.lastActivityDate());
            }
            if (updateInfo.getUpdatedAt().isAfter(link.getLastCheck())) {
                LinkUpdateRequest linkUpdateRequest = getLinkUpdateRequest(link, updateInfo);
                botClient.sendUpdate(linkUpdateRequest);
                log.info("Send update {} to bot", linkUpdateRequest);

                if (question != null) {
                    questionRepository.update(question);
                }

                updatesCount++;
            }

            link.setLastCheck(OffsetDateTime.now(ZoneId.systemDefault()));
            linkRepository.update(link);
        }

        return updatesCount;
    }

    private LinkUpdateRequest getLinkUpdateRequest(Link link, UpdateInfo updateInfo) {
        long id = link.getLinkId();
        String url = link.getUri().toString();
        Long[] tgChatIds = getTgChatsIdForUpdate(link);

        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(id, url, updateInfo.getDescription(), tgChatIds);
        return linkUpdateRequest;
    }

    private Long[] getTgChatsIdForUpdate(Link link) {
        List<Chat> chats = trackedLinkRepository.getAllChatsByLinkId(link.getLinkId());
        Long[] tgChatIds = new Long[chats.size()];
        for (int i = 0; i < chats.size(); i++) {
            tgChatIds[i] = chats.get(i).getTgChatId();
        }

        return tgChatIds;
    }

    private List<Link> getLinksToCheck() {
        OffsetDateTime now = OffsetDateTime.now();
        Predicate<Link> filter = link -> Duration.between(link.getLastCheck(), now).toHours() > CHECK_TIME;

        return linkRepository.getAll().stream()
            .filter(filter)
            .toList();
    }
}
