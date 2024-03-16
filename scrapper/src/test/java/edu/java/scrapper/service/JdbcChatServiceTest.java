package edu.java.scrapper.service;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.JdbcChatDao;
import edu.java.scrapper.dao.repository.JdbcLinkDao;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.exception.ReRegistrationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JdbcChatServiceTest extends IntegrationTest {
    @Autowired
    private ChatService chatService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private JdbcChatDao chatDao;
    @Autowired
    private JdbcLinkDao linkDao;

    @Test
    @Transactional
    @Rollback
    void shouldSaveChat() {
        Chat expected = new Chat();
        expected.setTgChatId(1L);

        chatService.register(1L);

        Chat actual = chatDao.getByTgChatId(1L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowReRegistrationException() {
        chatService.register(1L);

        assertThrows(ReRegistrationException.class, () -> chatService.register(1L));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteChat() {
        List<Chat> expected = List.of(new Chat());
        expected.getFirst().setTgChatId(2L);

        chatService.register(1L);
        chatService.register(2L);
        chatService.unregister(1L);

        List<Chat> actual = chatDao.getAll();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnEmptyLinkListIfChatUnregistered() throws URISyntaxException {
        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        chatService.unregister(1L);

        List<Link> actual = linkDao.getAll();

        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteUntrackedLinksIfChatUnregistered() throws URISyntaxException {
        List<URI> expected = List.of(
            new URI("https://github.com/IA1I/java-course-2023-backend")
        );
        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        chatService.unregister(1L);

        List<Link> actual = linkDao.getAll();

        Assertions.assertThat(actual).extracting(Link::getUri).isEqualTo(expected);
    }
}
