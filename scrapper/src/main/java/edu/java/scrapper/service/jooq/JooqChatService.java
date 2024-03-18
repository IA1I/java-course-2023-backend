package edu.java.scrapper.service.jooq;

import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.repository.jooq.JooqTrackedLinkRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.exception.ReRegistrationException;
import edu.java.scrapper.service.ChatService;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class JooqChatService implements ChatService {
    private final JooqChatRepository chatRepository;
    private final JooqLinkRepository linkRepository;
    private final JooqTrackedLinkRepository trackedLinkRepository;

    @Autowired
    public JooqChatService(
        JooqChatRepository chatRepository,
        JooqLinkRepository linkRepository,
        JooqTrackedLinkRepository trackedLinkRepository
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
        chatRepository.deleteByTgChatId(tgChatId);

        List<Link> links = linkRepository.getAll();
        List<Link> trackedLinks = trackedLinkRepository.getAllDistinctLinks();
        links.removeAll(trackedLinks);

        for (Link link : links) {
            linkRepository.delete(link.getLinkId());
        }
    }
}
