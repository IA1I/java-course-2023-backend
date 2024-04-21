package edu.java.scrapper.dao.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.entity.ChatEntity;
import edu.java.scrapper.dao.entity.LinkEntity;
import edu.java.scrapper.dao.repository.jpa.JpaChatRepository;
import edu.java.scrapper.dao.repository.jpa.JpaLinkRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = "app.access-type=jpa")
public class JpaLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JpaChatRepository chatRepository;
    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldSaveLink() {
        LinkEntity expected = new LinkEntity();
        expected.setUri("https://github.com/IA1I/java-course-2023-backend");
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        linkRepository.save(expected);

        List<LinkEntity> links = linkRepository.findAll();
        LinkEntity actual = links.getFirst();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnAllLinks() {
        LinkEntity link1 = new LinkEntity();
        link1.setUri("https://github.com/IA1I/java-course-2023-backend");
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        LinkEntity link2 = new LinkEntity();
        link2.setUri("https://github.com/IA1I/tinkoff_edu2023");
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        LinkEntity link3 = new LinkEntity();
        link3.setUri("https://github.com/sanyarnd/java-course-2023-backend-template");
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        List<LinkEntity> expected = List.of(link1, link2, link3);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        List<LinkEntity> actual = linkRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteLinkAndReturnAllLinks() {
        LinkEntity link1 = new LinkEntity();
        link1.setUri("https://github.com/IA1I/java-course-2023-backend");
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        LinkEntity link2 = new LinkEntity();
        link2.setUri("https://github.com/IA1I/tinkoff_edu2023");
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2000000000L), ZoneOffset.UTC));
        LinkEntity link3 = new LinkEntity();
        link3.setUri("https://github.com/sanyarnd/java-course-2023-backend-template");
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2100000000L), ZoneOffset.UTC));

        List<LinkEntity> expected = List.of(link2, link3);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        List<LinkEntity> links = linkRepository.findAll();
        linkRepository.delete(links.getFirst());

        List<LinkEntity> actual = linkRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetLinkById() {
        LinkEntity expected = new LinkEntity();
        expected.setUri("https://github.com/IA1I/java-course-2023-backend");
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(expected);
        List<LinkEntity> links = linkRepository.findAll();
        LinkEntity link = links.getFirst();

        Optional<LinkEntity> actual = linkRepository.findById(link.getLinkId());

        Assertions.assertThat(actual.orElse(null)).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetLinkByURI() {
        LinkEntity expected = new LinkEntity();
        expected.setUri("https://github.com/IA1I/java-course-2023-backend");
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(expected);

        Optional<LinkEntity> actual = linkRepository.findByUri("https://github.com/IA1I/java-course-2023-backend");

        Assertions.assertThat(actual.orElse(null)).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnTrueForExistingLink() {
        LinkEntity link = new LinkEntity();
        link.setUri("https://github.com/IA1I/java-course-2023-backend");
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(link);

        Boolean actual = linkRepository.existsByUri("https://github.com/IA1I/java-course-2023-backend");

        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateLink() {
        LinkEntity link = new LinkEntity();
        link.setUri("https://github.com/IA1I/java-course-2023-backend");
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));

        linkRepository.save(link);
        LinkEntity expected = linkRepository.findByUri("https://github.com/IA1I/java-course-2023-backend").orElse(null);
        expected.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1800000000L), ZoneOffset.UTC));
        expected.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1800000000L), ZoneOffset.UTC));

        linkRepository.save(expected);

        LinkEntity actual = linkRepository.findById(expected.getLinkId()).orElse(null);

        Assertions.assertThat(actual.getUri()).isEqualTo(expected.getUri());
        Assertions.assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
        Assertions.assertThat(actual.getLastCheck()).isEqualTo(expected.getLastCheck());
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnListLinksToUpdate() {
        LinkEntity link1 = new LinkEntity();
        link1.setUri("https://github.com/IA1I/java-course-2023-backend");
        link1.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        link1.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153L), ZoneOffset.UTC));
        LinkEntity link2 = new LinkEntity();
        link2.setUri("https://github.com/IA1I/tinkoff_edu2023");
        link2.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1638396085L), ZoneOffset.UTC));
        link2.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1638396085L), ZoneOffset.UTC));
        LinkEntity link3 = new LinkEntity();
        link3.setUri("https://github.com/sanyarnd/java-course-2023-backend-template");
        link3.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2700000000L), ZoneOffset.UTC));
        link3.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2700000000L), ZoneOffset.UTC));

        List<LinkEntity> expected = List.of(link1, link2);
        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        List<LinkEntity> actual = linkRepository.findAllByLastCheckLessThan(OffsetDateTime.now());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnFalseForNotExistingLink() {
        Boolean actual = linkRepository.existsByUri("https://github.com/IA1I/java-course-2023-backend");

        Assertions.assertThat(actual).isFalse();
    }

}
