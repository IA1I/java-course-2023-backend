package edu.java.scrapper.service;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JdbcLinkServiceTest extends IntegrationTest {
    @Autowired
    private ChatService chatService;
    @Autowired
    private LinkService linkService;

    @Test
    @Transactional
    @Rollback
    void shouldThrowChatIsNotRegisteredExceptionForAdd() {
        Assertions.assertThrows(
            ChatIsNotRegisteredException.class,
            () -> linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );

    }

    @Test
    @Transactional
    @Rollback
    void shouldAddLink() throws URISyntaxException {
        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        List<Link> links = linkService.listAll(1L);
        Link actual = links.getFirst();

        assertThat(actual.getUri()).isEqualTo(expected.getUri());
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowReAddLinkExceptionForAdd() throws URISyntaxException {
        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        Assertions.assertThrows(
            ReAddLinkException.class,
            () -> linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowChatIsNotRegisteredExceptionForDelete() {
        Assertions.assertThrows(
            ChatIsNotRegisteredException.class,
            () -> linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowLinkIsNotTrackedExceptionForDelete() {
        chatService.register(1L);

        Assertions.assertThrows(
            LinkIsNotTrackedException.class,
            () -> linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldUntrackLinkButNotDelete() throws URISyntaxException {
        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        List<Link> actual = linkService.listAll(1L);

        assertThat(actual).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldUntrackLinkAndDelete() throws URISyntaxException {
        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        chatService.register(1L);
        chatService.register(2L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(2L, new URI("https://github.com/IA1I/tinkoff_edu2023"));

        linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));

        List<Link> actual = linkService.listAll(1L);

        assertThat(actual).isEmpty();
        Assertions.assertThrows(
            LinkIsNotTrackedException.class,
            () -> linkService.delete(1L, new URI("https://github.com/IA1I/java-course-2023-backend"))
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowChatIsNotRegisteredExceptionForListALl() {
        Assertions.assertThrows(ChatIsNotRegisteredException.class, () -> linkService.listAll(1L));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnAllTrackedLinksByChat() throws URISyntaxException {
        List<URI> expected = List.of(
            new URI("https://github.com/IA1I/java-course-2023-backend"),
            new URI("https://github.com/IA1I/tinkoff_edu2023"),
            new URI("https://github.com/sanyarnd/java-course-2023-backend-template")
        );

        chatService.register(1L);
        linkService.add(1L, new URI("https://github.com/IA1I/java-course-2023-backend"));
        linkService.add(1L, new URI("https://github.com/IA1I/tinkoff_edu2023"));
        linkService.add(1L, new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));

        List<Link> actual = linkService.listAll(1L);

        assertThat(actual).extracting(Link::getUri).containsExactlyInAnyOrderElementsOf(expected);
    }
}
