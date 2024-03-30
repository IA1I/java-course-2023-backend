package edu.java.scrapper.service.jpa;

import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.entity.ChatEntity;
import edu.java.scrapper.dao.entity.LinkEntity;
import edu.java.scrapper.dao.entity.QuestionEntity;
import edu.java.scrapper.dao.repository.jpa.JpaChatRepository;
import edu.java.scrapper.dao.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.dto.response.UpdateResponse;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.jdbc.JdbcUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
public class JpaLinkService implements LinkService {
    private static final long HOURS_TO_MINUS = 4L;
    private final JpaChatRepository chatRepository;
    private final JpaLinkRepository linkRepository;
    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;

    public JpaLinkService(
        JpaChatRepository chatRepository,
        JpaLinkRepository linkRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.githubClient = githubClient;
        this.stackOverflowClient = stackOverflowClient;
    }

    @Override
    @Transactional
    public Link add(long tgChatId, URI uri) {
        ChatEntity chat = getChat(tgChatId);

        if (!linkRepository.existsByUri(uri.toString())) {
            saveLink(uri);
        }

        LinkEntity link = getLink(uri);

        if (chat.getLinks().contains(link)) {
            throw new ReAddLinkException("Link is already being tracked");
        }

        chat.getLinks().add(link);
        link.getChats().add(chat);
        chatRepository.save(chat);
        linkRepository.save(link);

        return linkEntityToLink(link);
    }

    @Override
    @Transactional
    public Link delete(long tgChatId, URI uri) {
        ChatEntity chat = getChat(tgChatId);
        LinkEntity link = getLink(uri);
        chat.getLinks().remove(link);
        link.getChats().remove(chat);
        chatRepository.save(chat);
        linkRepository.save(link);
        deleteLinks();
        return linkEntityToLink(link);
    }

    @Override
    @Transactional
    public List<Link> listAll(long tgChatId) {
        ChatEntity chat = getChat(tgChatId);
        List<Link> links = new ArrayList<>();

        for (LinkEntity link : chat.getLinks()) {
            links.add(linkEntityToLink(link));
        }

        return links;
    }

    private void saveLink(URI uri) {
        LinkEntity link = new LinkEntity();
        QuestionEntity question = null;
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

            question = new QuestionEntity();

            question.setCommentsCount(commentResponse.getCommentsCount());
            question.setAnswersCount(questionResponse.getItem().answerCount());
        }
        link.setUri(uri.toString());
        link.setLastCheck(OffsetDateTime.now(ZoneId.systemDefault()).minusHours(HOURS_TO_MINUS));

        try {
            if (question != null) {
                question.setLink(link);

                link.setQuestion(question);
                log.info("Save question for link: {} in DB", link.getUri());
            }

            linkRepository.save(link);
            log.info("Save link: {} in DB", link.getUri());
        } catch (DuplicateKeyException ignored) {
            log.info("Link {} already saved", link.getUri());
        }
    }

    private LinkEntity getLink(URI uri) {
        Optional<LinkEntity> optionalLink = linkRepository.findByUri(uri.toString());

        if (optionalLink.isEmpty()) {
            log.error("Link: {} is not tracked", uri);
            throw new LinkIsNotTrackedException("Link is not tracked");
        }

        log.info("Get link: {}", uri);
        return optionalLink.get();
    }

    private ChatEntity getChat(long tgChatId) {
        Optional<ChatEntity> optionalChat = chatRepository.findByTgChatId(tgChatId);
        if (optionalChat.isEmpty()) {
            log.error("Chat: {} is not registered", tgChatId);
            throw new ChatIsNotRegisteredException("Chat is not registered");
        }

        log.info("Get chat {} from DB", tgChatId);
        return optionalChat.get();
    }

    private void deleteLinks() {
        List<LinkEntity> links = linkRepository.findAll();
        List<LinkEntity> trackedLinks = linkRepository.findDistinctTrackedLinks();
        links.removeAll(trackedLinks);

        for (LinkEntity link : links) {
            linkRepository.delete(link);
            log.info("Delete link: {} from DB", link.getUri());
        }
    }

    private Link linkEntityToLink(LinkEntity link) {
        try {
            return new Link(link.getLinkId(), new URI(link.getUri()), link.getUpdatedAt(), link.getLastCheck());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
