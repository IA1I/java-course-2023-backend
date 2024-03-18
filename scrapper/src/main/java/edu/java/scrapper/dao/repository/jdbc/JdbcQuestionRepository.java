package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.dao.mapper.QuestionRowMapper;
import edu.java.scrapper.dto.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcQuestionRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcQuestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Question question) {
        jdbcTemplate.update(
            "INSERT INTO question VALUES (?, ?, ?)",
            question.getLinkId(),
            question.getCommentsCount(),
            question.getAnswersCount()
        );
    }

    public void update(Question question) {
        jdbcTemplate.update(
            "UPDATE question SET comments_count = ?, answers_count = ? WHERE link_id = ?",
            question.getCommentsCount(),
            question.getAnswersCount(),
            question.getLinkId()
            );
    }

    public Question get(long linkId) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM question WHERE link_id = ?",
            new QuestionRowMapper(),
            linkId
        );
    }
}
