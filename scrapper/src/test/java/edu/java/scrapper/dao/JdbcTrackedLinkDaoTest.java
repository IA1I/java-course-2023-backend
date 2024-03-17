package edu.java.scrapper.dao;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.JdbcChatDao;
import edu.java.scrapper.dao.repository.JdbcLinkDao;
import edu.java.scrapper.dao.repository.JdbcTrackedLinkDao;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@SpringBootTest
public class JdbcTrackedLinkDaoTest extends IntegrationTest {
    @Autowired
    private JdbcChatDao chatDao;
    @Autowired
    private JdbcLinkDao linkDao;
    @Autowired
    private JdbcTrackedLinkDao trackedLinkDao;

    @Test
    @Transactional
    @Rollback
    void shouldSaveTrackedLink() throws URISyntaxException {
        Integer expected = 1;
        Chat chat = new Chat();
        chat.setTgChatId(1L);
        Link link = new Link();
        link.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        chatDao.save(chat);
        linkDao.save(link);
        Chat chatFromDB = chatDao.getAll().getFirst();
        Link linkFromDB = linkDao.getAll().getFirst();

        trackedLinkDao.save(chatFromDB.getChatId(), linkFromDB.getLinkId());

        Integer actual = trackedLinkDao.getNumberOfLinksById(linkFromDB.getLinkId());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteTrackedLink() throws URISyntaxException {
        Integer expected = 0;
        Chat chat = new Chat();
        chat.setTgChatId(2L);
        Link link = new Link();
        link.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));

        chatDao.save(chat);
        linkDao.save(link);
        Chat chats = chatDao.getAll().getFirst();
        Link linkFromDB = linkDao.getAll().getFirst();

        trackedLinkDao.save(chats.getChatId(), linkFromDB.getLinkId());
        trackedLinkDao.delete(chats.getChatId(), linkFromDB.getLinkId());

        Integer actual = trackedLinkDao.getNumberOfLinksById(linkFromDB.getLinkId());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnDistinctTrackedLinks() throws URISyntaxException {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        chat1.setTgChatId(3L);
        chat2.setTgChatId(4L);

        Link link1 = new Link();
        Link link2 = new Link();
        link1.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link2.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        List<Link> expected = List.of(link1);

        chatDao.save(chat1);
        chatDao.save(chat2);
        linkDao.save(link1);
        linkDao.save(link2);
        List<Chat> chats = chatDao.getAll();
        Link linkFromDB = linkDao.getAll().getFirst();
        trackedLinkDao.save(chats.get(0).getChatId(), linkFromDB.getLinkId());
        trackedLinkDao.save(chats.get(1).getChatId(), linkFromDB.getLinkId());

        List<Link> actual = trackedLinkDao.getAllDistinctLinks();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnTrackedLinksByChatId() throws URISyntaxException {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        chat1.setTgChatId(5L);
        chat2.setTgChatId(6L);

        Link link1 = new Link();
        Link link2 = new Link();
        Link link3 = new Link();
        link1.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link2.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link3.setUri(new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        List<Link> expected = List.of(link1, link2);

        chatDao.save(chat1);
        chatDao.save(chat2);
        linkDao.save(link1);
        linkDao.save(link2);
        linkDao.save(link3);
        List<Chat> chats = chatDao.getAll();
        List<Link> links = linkDao.getAll();
        trackedLinkDao.save(chats.get(0).getChatId(), links.get(0).getLinkId());
        trackedLinkDao.save(chats.get(0).getChatId(), links.get(1).getLinkId());
        trackedLinkDao.save(chats.get(1).getChatId(), links.get(2).getLinkId());

        List<Link> actual = trackedLinkDao.getAllLinksByChatId(chats.get(0).getChatId());

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnChatsThatTrackingLink() throws URISyntaxException {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        Chat chat3 = new Chat();
        chat1.setTgChatId(7L);
        chat2.setTgChatId(8L);
        chat3.setTgChatId(9L);

        Link link1 = new Link();
        Link link2 = new Link();
        Link link3 = new Link();
        link1.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link2.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link3.setUri(new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        List<Chat> expected = List.of(chat1, chat2);

        chatDao.save(chat1);
        chatDao.save(chat2);
        chatDao.save(chat3);
        linkDao.save(link1);
        linkDao.save(link2);
        linkDao.save(link3);
        List<Chat> chats = chatDao.getAll();
        List<Link> links = linkDao.getAll();
        trackedLinkDao.save(chats.get(0).getChatId(), links.get(0).getLinkId());
        trackedLinkDao.save(chats.get(1).getChatId(), links.get(0).getLinkId());
        trackedLinkDao.save(chats.get(1).getChatId(), links.get(1).getLinkId());
        trackedLinkDao.save(chats.get(2).getChatId(), links.get(2).getLinkId());


        List<Chat> actual = trackedLinkDao.getAllChatsByLinkId(links.get(0).getLinkId());

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
