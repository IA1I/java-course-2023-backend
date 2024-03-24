package edu.java.scrapper.service.jpa;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.entity.ChatEntity;
import edu.java.scrapper.dao.entity.LinkEntity;
import edu.java.scrapper.dao.entity.QuestionEntity;
import edu.java.scrapper.dao.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.dto.request.LinkUpdateRequest;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.dto.response.UpdateResponse;
import edu.java.scrapper.dto.update_mapper.UpdateInfo;
import edu.java.scrapper.service.LinkUpdater;
import edu.java.scrapper.service.jdbc.JdbcUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
public class JpaLinksUpdaterService implements LinkUpdater {
    private static final long CHECK_TIME = 3L;
    private final JpaLinkRepository linkRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    public JpaLinksUpdaterService(
        JpaLinkRepository linkRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        BotClient botClient
    ) {
        this.linkRepository = linkRepository;
        this.githubClient = githubClient;
        this.stackOverflowClient = stackOverflowClient;
        this.botClient = botClient;
    }

    @Override
    @Transactional
    public int update() {
        int updatesCount = 0;
        List<LinkEntity> links = getLinksToCheck();
//        List<Link> links = linkRepository.getLinksToCheck();
        log.info("Get links {}", links);

        for (LinkEntity link : links) {
            QuestionEntity question = null;
            UpdateInfo updateInfo = new UpdateInfo();
            URI uri = getUri(link);

            if (uri.getHost().equals("github.com")) {
                UpdateResponse updateResponse = JdbcUtils.getUpdate(uri, githubClient);
                log.info("Get github update response {}", updateResponse);

                updateInfo.setUpdatedAt(updateResponse.timestamp());
                updateInfo.setDescription(updateResponse.activityType());

                link.setUpdatedAt(updateResponse.timestamp());
            } else if (uri.getHost().equals("stackoverflow.com")) {
                QuestionResponse questionResponse = JdbcUtils.getQuestionResponse(uri, stackOverflowClient);
                QuestionResponse.Item itemResponse = questionResponse.getItem();
                log.info("Get question response {}", questionResponse);

                CommentResponse commentResponse = JdbcUtils.getCommentResponse(uri, stackOverflowClient);
                log.info("Get comment response {}", commentResponse);
                question = link.getQuestion();

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
                question.setCommentsCount(commentResponse.getCommentsCount());
                question.setAnswersCount(itemResponse.answerCount());
            }
            if (updateInfo.getUpdatedAt().isAfter(link.getLastCheck())) {
                LinkUpdateRequest linkUpdateRequest = getLinkUpdateRequest(link, updateInfo);
                botClient.sendUpdate(linkUpdateRequest);
                log.info("Send update {} to bot", linkUpdateRequest);

                updatesCount++;
            }

            link.setLastCheck(OffsetDateTime.now(ZoneId.systemDefault()));
            linkRepository.save(link);
        }

        return updatesCount;
    }

    private URI getUri(LinkEntity link) {
        URI uri;
        try {
            uri = new URI(link.getUri());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    private LinkUpdateRequest getLinkUpdateRequest(LinkEntity link, UpdateInfo updateInfo) {
        long id = link.getLinkId();
        String url = link.getUri();
        Long[] tgChatIds = getTgChatsIdForUpdate(link);

        return new LinkUpdateRequest(id, url, updateInfo.getDescription(), tgChatIds);
    }

    private Long[] getTgChatsIdForUpdate(LinkEntity link) {
        List<ChatEntity> chats = new ArrayList<>(link.getChats());
        Long[] tgChatIds = new Long[chats.size()];
        for (int i = 0; i < chats.size(); i++) {
            tgChatIds[i] = chats.get(i).getTgChatId();
        }

        return tgChatIds;
    }

    private List<LinkEntity> getLinksToCheck() {
        OffsetDateTime now = OffsetDateTime.now();
        Predicate<LinkEntity> filter = link -> Duration.between(link.getLastCheck(), now).toHours() > CHECK_TIME;

        return linkRepository.findAll().stream()
            .filter(filter)
            .toList();
    }
}
