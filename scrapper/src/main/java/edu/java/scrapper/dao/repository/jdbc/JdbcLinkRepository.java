package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.dao.mapper.LinkRowMapper;
import edu.java.scrapper.dto.Link;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLinkRepository {
    private static final String SELECT_FROM_LINK_BY_ID = "SELECT * FROM link WHERE link_id = ?";
    private static final String SELECT_FROM_LINK_BY_URI = "SELECT * FROM link WHERE uri = ?";
    private static final String SELECT_ALL_FROM_LINK = "SELECT * FROM link";
    private static final String INSERT_INTO_LINK = "INSERT INTO link VALUES (DEFAULT, ?, ?, ?)";
    private static final String UPDATE_LINK_FIELDS_UPDATED_AT_AND_LAST_CHECK =
        "UPDATE link SET updated_at = ?, last_check = ? WHERE link_id = ?";
    private static final String DELETE_FROM_LINK_BY_ID = "DELETE FROM link WHERE link_id = ?";
    private static final String SELECT_EXISTS = "SELECT exists (SELECT TRUE FROM link WHERE uri = ?)";
    private static final String SELECT_FROM_LINK_TO_CHECK =
        "SELECT * FROM link WHERE now() - last_check > interval '3 hour'";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Link get(long id) {
        return jdbcTemplate.queryForObject(SELECT_FROM_LINK_BY_ID, new LinkRowMapper(), id);
    }

    public Link getByURI(URI uri) {
        return jdbcTemplate.queryForObject(SELECT_FROM_LINK_BY_URI, new LinkRowMapper(), uri.toString());
    }

    public List<Link> getAll() {
        return jdbcTemplate.query(SELECT_ALL_FROM_LINK, new LinkRowMapper());
    }

    public void save(Link entity) {
        jdbcTemplate.update(
            INSERT_INTO_LINK,
            entity.getUri().toString(),
            entity.getUpdatedAt(),
            entity.getLastCheck()
        );
    }

    public void update(Link entity) {
        jdbcTemplate.update(
            UPDATE_LINK_FIELDS_UPDATED_AT_AND_LAST_CHECK,
            entity.getUpdatedAt(),
            entity.getLastCheck(),
            entity.getLinkId()
        );
    }

    public void delete(long id) {
        jdbcTemplate.update(DELETE_FROM_LINK_BY_ID, id);
    }

    public Boolean exists(URI uri) {
        return jdbcTemplate.queryForObject(
            SELECT_EXISTS,
            Boolean.class,
            uri.toString()
        );
    }

    public List<Link> getLinksToCheck() {
        return jdbcTemplate.query(
            SELECT_FROM_LINK_TO_CHECK,
            new LinkRowMapper()
        );
    }
}
