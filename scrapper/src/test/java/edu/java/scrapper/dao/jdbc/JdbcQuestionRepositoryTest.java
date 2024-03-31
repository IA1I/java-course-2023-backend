package edu.java.scrapper.dao.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.Question;
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

@SpringBootTest(properties = "app.access-type=jdbc")
public class JdbcQuestionRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcQuestionRepository questionRepository;

    @Test
    @Transactional
    @Rollback
    void shouldSaveLinkQuestion() throws URISyntaxException {
        Question actual = new Question(0, 1, 1);
        Link link = new Link();
        link.setUri(new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2005410153L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2005410153L), ZoneOffset.UTC));
        linkRepository.save(link);

        Link linkFromDB = linkRepository.getByURI(new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        actual.setLinkId(linkFromDB.getLinkId());

        questionRepository.save(actual);

        Question expected = questionRepository.get(linkFromDB.getLinkId());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldUpdateQuestion() throws URISyntaxException {
        Question actual = new Question(0, 1, 1);
        Link link = new Link();
        link.setUri(new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        link.setUpdatedAt(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2005410153L), ZoneOffset.UTC));
        link.setLastCheck(OffsetDateTime.ofInstant(Instant.ofEpochSecond(2005410153L), ZoneOffset.UTC));
        linkRepository.save(link);

        Link linkFromDB = linkRepository.getByURI(new URI("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c-c"));
        actual.setLinkId(linkFromDB.getLinkId());

        questionRepository.save(actual);
        actual.setAnswersCount(2);
        actual.setCommentsCount(3);

        questionRepository.update(actual);
        Question expected = questionRepository.get(linkFromDB.getLinkId());

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
