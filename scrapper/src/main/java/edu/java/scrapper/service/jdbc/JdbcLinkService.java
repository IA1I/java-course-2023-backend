package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.dao.repository.JdbcChatDao;
import edu.java.scrapper.dao.repository.JdbcLinkDao;
import edu.java.scrapper.dao.repository.JdbcTrackedLinkDao;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class JdbcLinkService implements LinkService {
    private static final long HOURS_TO_MINUS = 4L;
    private final JdbcChatDao chatDao;
    private final JdbcLinkDao linkDao;
    private final JdbcTrackedLinkDao trackedLinkDao;

    @Autowired
    public JdbcLinkService(JdbcChatDao chatDao, JdbcLinkDao linkDao, JdbcTrackedLinkDao trackedLinkDao) {
        this.chatDao = chatDao;
        this.linkDao = linkDao;
        this.trackedLinkDao = trackedLinkDao;
    }

    @Override
    @Transactional
    public Link add(long tgChatId, URI uri) {
        Chat chat = getChat(tgChatId);

        if (!linkDao.exists(uri)) {
            saveLink(uri);
        }

        Link link = getLink(uri);

        try {
            trackedLinkDao.save(chat.getChatId(), link.getLinkId());
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

        trackedLinkDao.delete(chat.getChatId(), link.getLinkId());
        deleteLinks();

        return link;
    }

    @Override
    @Transactional
    public List<Link> listAll(long tgChatId) {
        Chat chat = getChat(tgChatId);
        log.info("Get all tracked links by chat: {}", tgChatId);
        return trackedLinkDao.getAllLinksByChatId(chat.getChatId());
    }

    private void saveLink(URI uri) {
        Link link = new Link();
        link.setUri(uri);
        link.setUpdatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
        link.setLastCheck(OffsetDateTime.now(ZoneId.systemDefault()).minusHours(HOURS_TO_MINUS));
        try {
            linkDao.save(link);
            log.info("Save link: {} in DB", link.getUri());
        } catch (DuplicateKeyException ignored) {
            log.info("Link {} already saved", link.getUri());
        }
    }

    private Chat getChat(long tgChatId) {
        Chat chat;
        try {
            chat = chatDao.getByTgChatId(tgChatId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Chat: {} is not registered", tgChatId);
            throw new ChatIsNotRegisteredException("Chat is not registered");
        }
        log.info("Get chat {} from DB", chat.getTgChatId());
        return chat;
    }

    private Link getLink(URI uri) {
        Link link;
        try {
            link = linkDao.getByURI(uri);
            log.info("Get link: {}", link.getUri());
        } catch (EmptyResultDataAccessException e) {
            log.error("Link: {} is not tracked", uri);
            throw new LinkIsNotTrackedException("Link is not tracked");
        }

        return link;
    }

    private void deleteLinks() {
        List<Link> links = linkDao.getAll();
        List<Link> trackedLinks = trackedLinkDao.getAllDistinctLinks();
        links.removeAll(trackedLinks);

        for (Link link : links) {
            linkDao.delete(link.getLinkId());
            log.info("Deleted link: {}", link.getUri());
        }
    }
}
