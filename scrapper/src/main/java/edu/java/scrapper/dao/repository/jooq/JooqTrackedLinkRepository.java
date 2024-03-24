package edu.java.scrapper.dao.repository.jooq;

import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.Link;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.dao.jooq.Tables.CHAT;
import static edu.java.scrapper.dao.jooq.Tables.LINK;
import static edu.java.scrapper.dao.jooq.Tables.TRACKED_LINK;

@Repository
public class JooqTrackedLinkRepository {
    private final DSLContext dslContext;

    @Autowired
    public JooqTrackedLinkRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void save(long chatId, long linkId) {
        dslContext.insertInto(TRACKED_LINK)
            .set(TRACKED_LINK.CHAT_ID, chatId)
            .set(TRACKED_LINK.LINK_ID, linkId)
            .execute();
    }

    public void delete(long chatId, long linkId) {
        dslContext.deleteFrom(TRACKED_LINK)
            .where(TRACKED_LINK.CHAT_ID.eq(chatId))
            .and(TRACKED_LINK.LINK_ID.eq(linkId))
            .execute();
    }

    public List<Link> getAllLinksByChatId(long chatId) {
        return dslContext.select()
            .from(TRACKED_LINK)
            .leftJoin(LINK).on(TRACKED_LINK.LINK_ID.eq(LINK.LINK_ID))
            .where(TRACKED_LINK.CHAT_ID.eq(chatId))
            .fetchInto(Link.class);
    }

    public Integer getNumberOfLinksById(long linkId) {
        return dslContext.select(DSL.count())
            .from(TRACKED_LINK)
            .where(TRACKED_LINK.LINK_ID.eq(linkId))
            .fetchOneInto(Integer.class);
    }

    public List<Link> getAllDistinctLinks() {
        return dslContext.selectDistinct(LINK.fields())
            .from(TRACKED_LINK)
            .leftJoin(LINK).on(TRACKED_LINK.LINK_ID.eq(LINK.LINK_ID))
            .fetchInto(Link.class);
    }

    public List<Chat> getAllChatsByLinkId(long linkId) {
        return dslContext.select()
            .from(TRACKED_LINK)
            .leftJoin(CHAT).on(TRACKED_LINK.CHAT_ID.eq(CHAT.ID))
            .where(TRACKED_LINK.LINK_ID.eq(linkId))
            .fetchInto(Chat.class);
    }
}
