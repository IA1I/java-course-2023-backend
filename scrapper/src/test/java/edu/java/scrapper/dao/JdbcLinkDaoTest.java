package edu.java.scrapper.dao;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.JdbcLinkDao;
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
public class JdbcLinkDaoTest extends IntegrationTest {
    @Autowired
    private JdbcLinkDao linkDao;

    @Test
    @Transactional
    @Rollback
    void shouldSaveLink() throws URISyntaxException {
        Link expected = new Link();
        expected.setUri(new URI("https://github.com/IA1I/java-course-2023-backend"));
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        linkDao.save(expected);

        List<Link> links = linkDao.getAll();
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
        linkDao.save(link1);
        linkDao.save(link2);
        linkDao.save(link3);

        List<Link> actual = linkDao.getAll();

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
        linkDao.save(link1);
        linkDao.save(link2);
        linkDao.save(link3);

        List<Link> links = linkDao.getAll();
        linkDao.delete(links.getFirst().getLinkId());

        List<Link> actual = linkDao.getAll();

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

        linkDao.save(expected);
        List<Link> links = linkDao.getAll();
        Link link = links.getFirst();

        Link actual = linkDao.get(link.getLinkId());

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

        linkDao.save(expected);

        Link actual = linkDao.getByURI(new URI("https://github.com/IA1I/java-course-2023-backend"));

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

        linkDao.save(link);

        Boolean actual = linkDao.exists(new URI("https://github.com/IA1I/java-course-2023-backend"));

        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnFalseForNotExistingLink() throws URISyntaxException {
        Boolean actual = linkDao.exists(new URI("https://github.com/IA1I/java-course-2023-backend"));

        Assertions.assertThat(actual).isFalse();
    }
}
