package edu.java.scrapper.dao.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dto.Link;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@SpringBootTest(properties = "app.access-type=jooq")
public class JooqLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JooqLinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldSaveLink() throws URISyntaxException {
        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        linkRepository.save(expected);

        List<Link> links = linkRepository.getAll();
        Link actual = links.getFirst();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnAllLinks() throws URISyntaxException {
        Link link1 = new Link();
        link1.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        Link link2 = new Link();
        link2.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        Link link3 = new Link();
        link3.setUri(new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        List<Link> expected = List.of(link1, link2, link3);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        List<Link> actual = linkRepository.getAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteLinkAndReturnAllLinks() throws URISyntaxException {
        Link link1 = new Link();
        link1.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        Link link2 = new Link();
        link2.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        Link link3 = new Link();
        link3.setUri(new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));

        List<Link> expected = List.of(link2, link3);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        List<Link> links = linkRepository.getAll();
        linkRepository.delete(links.getFirst().getLinkId());

        List<Link> actual = linkRepository.getAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetLinkById() throws URISyntaxException {
        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(expected);
        List<Link> links = linkRepository.getAll();
        Link link = links.getFirst();

        Link actual = linkRepository.get(link.getLinkId());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetLinkByURI() throws URISyntaxException {
        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(expected);

        Link actual = linkRepository.getByURI(new URI("https://github.com/IA1I/java-course-2023-backend"));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnTrueForExistingLink() throws URISyntaxException {
        Link link = new Link();
        link.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(link);

        Boolean actual = linkRepository.exists(new URI("https://github.com/IA1I/java-course-2023-backend"));

        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateLink() throws URISyntaxException {
        Link link = new Link();
        link.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(link);
        Link expected = linkRepository.getByURI(new URI("https://github.com/IA1I/java-course-2023-backend"));
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1800000000L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1800000000L), ZoneOffset.UTC));

        linkRepository.update(expected);

        Link actual = linkRepository.get(expected.getLinkId());

        Assertions.assertThat(actual.getUri()).isEqualTo(expected.getUri());
        Assertions.assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
        Assertions.assertThat(actual.getLastCheck()).isEqualTo(expected.getLastCheck());
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnListLinksToUpdate() throws URISyntaxException {
        Link link1 = new Link();
        link1.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        Link link2 = new Link();
        link2.setUri(new URI("https://github.com/IA1I/tinkoff_edu2023"));
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1638396085L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1638396085L), ZoneOffset.UTC));
        Link link3 = new Link();
        link3.setUri(new URI("https://github.com/sanyarnd/java-course-2023-backend-template"));
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2700000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2700000000L), ZoneOffset.UTC));
        List<Link> expected = List.of(link1, link2);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        List<Link> actual = linkRepository.getLinksToCheck();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnFalseForNotExistingLink() throws URISyntaxException {
        Boolean actual = linkRepository.exists(new URI("https://github.com/IA1I/java-course-2023-backend"));

        Assertions.assertThat(actual).isFalse();
    }
}
