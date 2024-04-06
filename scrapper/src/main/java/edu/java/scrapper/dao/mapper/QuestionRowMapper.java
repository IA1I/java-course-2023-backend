package edu.java.scrapper.dao.mapper;

import edu.java.scrapper.dto.Question;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class QuestionRowMapper implements RowMapper<Question> {
    @Override
    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
        long linkId = rs.getLong("link_id");
        int commentsCount = rs.getInt("comments_count");
        int answersCount = rs.getInt("answers_count");

        return new Question(linkId, commentsCount, answersCount);
    }
}
