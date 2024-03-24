package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcTrackedLinkRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.ReRegistrationException;
import edu.java.scrapper.service.ChatService;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
public class JdbcChatService implements ChatService {
    private final JdbcChatRepository chatRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcTrackedLinkRepository trackedLinkRepository;

    public JdbcChatService(
        JdbcChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        JdbcTrackedLinkRepository trackedLinkRepository
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.trackedLinkRepository = trackedLinkRepository;
    }

    @Override
    @Transactional
    public void register(long tgChatId) {
        Chat chat = new Chat();
        chat.setTgChatId(tgChatId);

        try {
            chatRepository.save(chat);
            log.info("Save chat: {} in DB", tgChatId);
        } catch (DuplicateKeyException e) {
            log.info("Chat {} already saved", tgChatId);
            throw new ReRegistrationException("Chat re-registration");
        }
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        try {
            chatRepository.getByTgChatId(tgChatId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Chat: {} is not registered", tgChatId);
            throw new ChatIsNotRegisteredException("Chat is not registered");
        }

        chatRepository.deleteByTgChatId(tgChatId);
        log.info("Delete chat: {} from DB", tgChatId);
        List<Link> links = linkRepository.getAll();
        List<Link> trackedLinks = trackedLinkRepository.getAllDistinctLinks();
        links.removeAll(trackedLinks);

        for (Link link : links) {
            linkRepository.delete(link.getLinkId());
            log.info("Delete link: {} from DB", link.getUri());
        }
    }
}
