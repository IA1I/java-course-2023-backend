package edu.java.scrapper.dao.repository.jooq;

import edu.java.scrapper.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.dao.jooq.Tables.LINK;

@Repository
public class JooqLinkRepository {
    private final DSLContext dslContext;

    @Autowired
    public JooqLinkRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Link get(long linkId) {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .where(LINK.LINK_ID.eq(linkId))
            .fetchOneInto(Link.class);
    }

    public Link getByURI(URI uri) {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .where(LINK.URI.eq(uri.toString()))
            .fetchOneInto(Link.class);
    }

    public List<Link> getAll() {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .fetchInto(Link.class);
    }

    public void save(Link link) {
        dslContext.insertInto(LINK)
            .set(LINK.URI, link.getUri().toString())
            .set(LINK.UPDATED_AT, link.getUpdatedAt())
            .set(LINK.LAST_CHECK, link.getLastCheck())
            .execute();
    }

    public void update(Link entity) {
        dslContext.update(LINK)
            .set(LINK.UPDATED_AT, entity.getUpdatedAt())
            .set(LINK.LAST_CHECK, entity.getLastCheck())
            .where(LINK.LINK_ID.eq(entity.getLinkId()))
            .execute();
    }

    public void delete(long linkId) {
        dslContext.deleteFrom(LINK)
            .where(LINK.LINK_ID.eq(linkId))
            .execute();
    }

    public Boolean exists(URI uri) {
        return dslContext.fetchExists(
            dslContext.selectOne()
                .from(LINK)
                .where(LINK.URI.eq(uri.toString()))
        );
    }

    public List<Link> getLinksToCheck() {
        return dslContext.select(LINK.fields())
            .from(LINK)
            .where(LINK.LAST_CHECK.lessThan(OffsetDateTime.now()))
            .fetchInto(Link.class);
    }
}
