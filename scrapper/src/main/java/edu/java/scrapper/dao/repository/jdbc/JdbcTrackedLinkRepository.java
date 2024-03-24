package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.dao.mapper.ChatRowMapper;
import edu.java.scrapper.dao.mapper.LinkRowMapper;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTrackedLinkRepository {
    private static final String INSERT_INTO_TRACKED_LINK = "INSERT INTO tracked_link VALUES (?, ?)";
    private static final String DELETE_FROM_TRACKED_LINK =
        "DELETE FROM tracked_link WHERE chat_id = ? AND link_id = ?";
    private static final String SELECT_FROM_TRACKED_LINK_LEFT_JOIN_LINK_BY_CHAT_ID =
        "SELECT * FROM tracked_link tl LEFT JOIN link l ON tl.link_id = l.link_id WHERE tl.chat_id = ?";
    private static final String SELECT_COUNT_FROM_TRACKED_LINK_BY_LINK_ID =
        "SELECT COUNT(*) FROM tracked_link WHERE link_id = ?";
    private static final String SELECT_DISTINCT_FROM_TRACKED_LINK_LEFT_JOIN_LINK =
        "SELECT DISTINCT l.* FROM tracked_link tl LEFT JOIN link l ON tl.link_id = l.link_id";
    private static final String SELECT_FROM_TRACKED_LINK_LEFT_JOIN_CHAT_BY_LINK_ID =
        "SELECT * FROM tracked_link tl LEFT JOIN chat c ON tl.chat_id = c.id WHERE link_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTrackedLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(long chatId, long linkId) {
        jdbcTemplate.update(INSERT_INTO_TRACKED_LINK, chatId, linkId);
    }

    public void delete(long chatId, long linkId) {
        jdbcTemplate.update(DELETE_FROM_TRACKED_LINK, chatId, linkId);
    }

    public List<Link> getAllLinksByChatId(long chatId) {
        return jdbcTemplate.query(
            SELECT_FROM_TRACKED_LINK_LEFT_JOIN_LINK_BY_CHAT_ID,
            new LinkRowMapper(),
            chatId
        );
    }

    public Integer getNumberOfLinksById(long linkId) {
        return jdbcTemplate.queryForObject(
            SELECT_COUNT_FROM_TRACKED_LINK_BY_LINK_ID,
            Integer.class,
            linkId
        );
    }

    public List<Link> getAllDistinctLinks() {
        return jdbcTemplate.query(
            SELECT_DISTINCT_FROM_TRACKED_LINK_LEFT_JOIN_LINK,
            new LinkRowMapper()
        );
    }

    public List<Chat> getAllChatsByLinkId(long linkId) {
        return jdbcTemplate.query(
            SELECT_FROM_TRACKED_LINK_LEFT_JOIN_CHAT_BY_LINK_ID,
            new ChatRowMapper(),
            linkId
        );
    }
}
