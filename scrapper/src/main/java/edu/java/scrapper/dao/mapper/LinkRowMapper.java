package edu.java.scrapper.dao.mapper;

import edu.java.scrapper.dto.Link;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.springframework.jdbc.core.RowMapper;

public class LinkRowMapper implements RowMapper<Link> {
    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        long lingId = rs.getLong("link_id");
        URI uri = getURI(rs);
        OffsetDateTime updatedAt = rs.getObject("updated_at", OffsetDateTime.class);
        OffsetDateTime lastCheck = rs.getObject("last_check", OffsetDateTime.class);

        return new Link(lingId, uri, updatedAt, lastCheck);
    }

    private URI getURI(ResultSet rs) throws SQLException {
        URI uri;
        try {
            uri = new URI(rs.getString("uri"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return uri;
    }
}
