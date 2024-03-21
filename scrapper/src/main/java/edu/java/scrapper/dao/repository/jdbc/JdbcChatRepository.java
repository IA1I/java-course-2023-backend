package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.dao.mapper.ChatRowMapper;
import edu.java.scrapper.dto.Chat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcChatRepository {
    private static final String SELECT_FROM_CHAT_BY_ID = "SELECT * FROM chat WHERE id = ?";
    private static final String SELECT_FROM_CHAT_BY_TG_CHAT_ID = "SELECT * FROM chat WHERE tg_chat_id = ?";
    private static final String SELECT_ALL_FROM_CHAT = "SELECT * FROM chat";
    private static final String INSERT_INTO_CHAT = "INSERT INTO chat VALUES (DEFAULT, ?)";
    private static final String DELETE_FROM_CHAT_BY_ID = "DELETE FROM chat WHERE id = ?";
    private static final String DELETE_FROM_CHAT_BY_TG_CHAT_ID = "DELETE FROM chat WHERE tg_chat_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Chat get(long id) {
        return jdbcTemplate.queryForObject(SELECT_FROM_CHAT_BY_ID, new ChatRowMapper(), id);
    }

    public Chat getByTgChatId(long tgChatId) {
        return jdbcTemplate.queryForObject(SELECT_FROM_CHAT_BY_TG_CHAT_ID, new ChatRowMapper(), tgChatId);
    }

    public List<Chat> getAll() {
        return jdbcTemplate.query(SELECT_ALL_FROM_CHAT, new ChatRowMapper());
    }

    public void save(Chat entity) {
        jdbcTemplate.update(INSERT_INTO_CHAT, entity.getTgChatId());
    }

    public void delete(long id) {
        jdbcTemplate.update(DELETE_FROM_CHAT_BY_ID, id);
    }

    public void deleteByTgChatId(long tgChatId) {
        jdbcTemplate.update(DELETE_FROM_CHAT_BY_TG_CHAT_ID, tgChatId);
    }
}
