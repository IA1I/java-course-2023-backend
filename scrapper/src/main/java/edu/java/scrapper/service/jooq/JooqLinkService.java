package edu.java.scrapper.service.jooq;

import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.repository.jooq.JooqQuestionRepository;
import edu.java.scrapper.dao.repository.jooq.JooqTrackedLinkRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.Question;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.dto.response.UpdateResponse;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.jdbc.JdbcUtils;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class JooqLinkService implements LinkService {
    private static final long HOURS_TO_MINUS = 4L;
    private final JooqChatRepository chatRepository;
    private final JooqLinkRepository linkRepository;
    private final JooqTrackedLinkRepository trackedLinkRepository;
    private final JooqQuestionRepository questionRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;

    public JooqLinkService(
        JooqChatRepository chatRepository,
        JooqLinkRepository linkRepository,
        JooqTrackedLinkRepository trackedLinkRepository,
        JooqQuestionRepository questionRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.trackedLinkRepository = trackedLinkRepository;
        this.questionRepository = questionRepository;
        this.githubClient = githubClient;
        this.stackOverflowClient = stackOverflowClient;
    }

    @Override
    @Transactional
    public Link add(long tgChatId, URI uri) {
        Chat chat = getChat(tgChatId);

        if (!linkRepository.exists(uri)) {
            saveLink(uri);
        }

        Link link = getLink(uri);

        try {
            trackedLinkRepository.save(chat.getChatId(), link.getLinkId());
        } catch (DuplicateKeyException e) {
            throw new ReAddLinkException("Link is already being tracked");
        }

        return link;
    }

    @Override
    @Transactional
    public Link delete(long tgChatId, URI uri) {
        Chat chat = getChat(tgChatId);
        Link link = getLink(uri);

        trackedLinkRepository.delete(chat.getChatId(), link.getLinkId());
        deleteLinks();

        return link;
    }

    @Override
    @Transactional
    public List<Link> listAll(long tgChatId) {
        Chat chat = getChat(tgChatId);
        log.info("Get all tracked links by chat: {}", tgChatId);
        return trackedLinkRepository.getAllLinksByChatId(chat.getChatId());
    }

    private void saveLink(URI uri) {
        Link link = new Link();
        Question question = null;
        if (uri.getHost().equals("github.com")) {
            UpdateResponse updateResponse = JdbcUtils.getUpdate(uri, githubClient);
            log.info("Get github updateResponse {}", updateResponse);

            link.setUpdatedAt(updateResponse.timestamp());
        } else if (uri.getHost().equals("stackoverflow.com")) {
            QuestionResponse questionResponse = JdbcUtils.getQuestionResponse(uri, stackOverflowClient);
            log.info("Get question response {}", questionResponse);

            CommentResponse commentResponse = JdbcUtils.getCommentResponse(uri, stackOverflowClient);
            log.info("Get comment response {}", commentResponse);

            link.setUpdatedAt(questionResponse.getItem().lastActivityDate());

            question = new Question();

            question.setCommentsCount(commentResponse.getCommentsCount());
            question.setAnswersCount(questionResponse.getItem().answerCount());
        }
        link.setUri(uri);
        link.setLastCheck(OffsetDateTime.now(ZoneId.systemDefault()).minusHours(HOURS_TO_MINUS));

        try {
            linkRepository.save(link);
            log.info("Save link: {} in DB", link.getUri());

            if (question != null) {
                question.setLinkId(linkRepository.getByURI(uri).getLinkId());
                questionRepository.save(question);
                log.info("Save question for link: {} in DB", link.getUri());
            }
        } catch (DuplicateKeyException ignored) {
            log.info("Link {} already saved", link.getUri());
        }
    }

    private Chat getChat(long tgChatId) {
        Chat chat = chatRepository.getByTgChatId(tgChatId);
        if (chat == null) {
            log.error("Chat: {} is not registered", tgChatId);
            throw new ChatIsNotRegisteredException("Chat is not registered");
        }

        log.info("Get chat {} from DB", chat.getTgChatId());
        return chat;
    }

    private Link getLink(URI uri) {
        Link link = linkRepository.getByURI(uri);
        if (link == null) {
            log.error("Link: {} is not tracked", uri);
            throw new LinkIsNotTrackedException("Link is not tracked");
        }
        log.info("Get link: {}", link.getUri());
        return link;
    }

    private void deleteLinks() {
        List<Link> links = linkRepository.getAll();
        List<Link> trackedLinks = trackedLinkRepository.getAllDistinctLinks();
        links.removeAll(trackedLinks);

        for (Link link : links) {
            linkRepository.delete(link.getLinkId());
            log.info("Deleted link: {}", link.getUri());
        }
    }
}
