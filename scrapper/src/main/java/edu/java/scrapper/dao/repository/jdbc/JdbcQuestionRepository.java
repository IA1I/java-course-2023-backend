package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.dao.mapper.QuestionRowMapper;
import edu.java.scrapper.dto.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcQuestionRepository {
    private static final String INSERT_INTO_QUESTION = "INSERT INTO question VALUES (?, ?, ?)";
    private static final String UPDATE_QUESTION =
        "UPDATE question SET comments_count = ?, answers_count = ? WHERE link_id = ?";
    private static final String SELECT_QUESTION_BY_LINK_ID = "SELECT * FROM question WHERE link_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcQuestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Question question) {
        jdbcTemplate.update(
            INSERT_INTO_QUESTION,
            question.getLinkId(),
            question.getCommentsCount(),
            question.getAnswersCount()
        );
    }

    public void update(Question question) {
        jdbcTemplate.update(
            UPDATE_QUESTION,
            question.getCommentsCount(),
            question.getAnswersCount(),
            question.getLinkId()
        );
    }

    public Question get(long linkId) {
        return jdbcTemplate.queryForObject(
            SELECT_QUESTION_BY_LINK_ID,
            new QuestionRowMapper(),
            linkId
        );
    }
}
