package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.dao.repository.JdbcChatDao;
import edu.java.scrapper.dao.repository.JdbcLinkDao;
import edu.java.scrapper.dao.repository.JdbcTrackedLinkDao;
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
public class JdbcChatService implements ChatService {
    private final JdbcChatDao chatDao;
    private final JdbcLinkDao linkDao;
    private final JdbcTrackedLinkDao trackedLinkDao;

    @Autowired
    public JdbcChatService(JdbcChatDao chatDao, JdbcLinkDao linkDao, JdbcTrackedLinkDao trackedLinkDao) {
        this.chatDao = chatDao;
        this.linkDao = linkDao;
        this.trackedLinkDao = trackedLinkDao;
    }

    @Override
    @Transactional
    public void register(long tgChatId) {
        Chat chat = new Chat();
        chat.setTgChatId(tgChatId);

        try {
            chatDao.save(chat);
            log.info("Save chat: {} in DB", tgChatId);
        } catch (DuplicateKeyException e) {
            log.info("Chat {} already saved", tgChatId);
            throw new ReRegistrationException("Chat re-registration");
        }
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        chatDao.deleteByTgChatId(tgChatId);
        List<Link> links = linkDao.getAll();
        List<Link> trackedLinks = trackedLinkDao.getAllDistinctLinks();
        links.removeAll(trackedLinks);

        for (Link link : links) {
            linkDao.delete(link.getLinkId());
        }
    }
}
