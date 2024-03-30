package edu.java.scrapper.dao.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcTrackedLinkRepository;
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

@SpringBootTest(properties = "app.access-type=jdbc")
public class JdbcTrackedLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcChatRepository chatRepository;
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcTrackedLinkRepository trackedLinkRepository;

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

        chatRepository.save(chat);
        linkRepository.save(link);
        Chat chatFromDB = chatRepository.getAll().getFirst();
        Link linkFromDB = linkRepository.getAll().getFirst();

        trackedLinkRepository.save(chatFromDB.getId(), linkFromDB.getLinkId());

        Integer actual = trackedLinkRepository.getNumberOfLinksById(linkFromDB.getLinkId());

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

        chatRepository.save(chat);
        linkRepository.save(link);
        Chat chats = chatRepository.getAll().getFirst();
        Link linkFromDB = linkRepository.getAll().getFirst();

        trackedLinkRepository.save(chats.getId(), linkFromDB.getLinkId());
        trackedLinkRepository.delete(chats.getId(), linkFromDB.getLinkId());

        Integer actual = trackedLinkRepository.getNumberOfLinksById(linkFromDB.getLinkId());

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

        chatRepository.save(chat1);
        chatRepository.save(chat2);
        linkRepository.save(link1);
        linkRepository.save(link2);
        List<Chat> chats = chatRepository.getAll();
        Link linkFromDB = linkRepository.getAll().getFirst();
        trackedLinkRepository.save(chats.get(0).getId(), linkFromDB.getLinkId());
        trackedLinkRepository.save(chats.get(1).getId(), linkFromDB.getLinkId());

        List<Link> actual = trackedLinkRepository.getAllDistinctLinks();
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

        chatRepository.save(chat1);
        chatRepository.save(chat2);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);
        List<Chat> chats = chatRepository.getAll();
        List<Link> links = linkRepository.getAll();
        trackedLinkRepository.save(chats.get(0).getId(), links.get(0).getLinkId());
        trackedLinkRepository.save(chats.get(0).getId(), links.get(1).getLinkId());
        trackedLinkRepository.save(chats.get(1).getId(), links.get(2).getLinkId());

        List<Link> actual = trackedLinkRepository.getAllLinksByChatId(chats.get(0).getId());

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

        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);
        List<Chat> chats = chatRepository.getAll();
        List<Link> links = linkRepository.getAll();
        trackedLinkRepository.save(chats.get(0).getId(), links.get(0).getLinkId());
        trackedLinkRepository.save(chats.get(1).getId(), links.get(0).getLinkId());
        trackedLinkRepository.save(chats.get(1).getId(), links.get(1).getLinkId());
        trackedLinkRepository.save(chats.get(2).getId(), links.get(2).getLinkId());


        List<Chat> actual = trackedLinkRepository.getAllChatsByLinkId(links.get(0).getLinkId());

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
