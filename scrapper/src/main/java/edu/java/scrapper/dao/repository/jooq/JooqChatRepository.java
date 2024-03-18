package edu.java.scrapper.dao.repository.jooq;

import edu.java.scrapper.dto.Chat;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.dao.jooq.Tables.CHAT;

@Repository
public class JooqChatRepository {
    private final DSLContext dslContext;

    @Autowired
    public JooqChatRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Chat get(long id) {
        return dslContext.select(CHAT.fields())
            .from(CHAT)
            .where(CHAT.CHAT_ID.eq(id))
            .fetchOneInto(Chat.class);
    }

    public Chat getByTgChatId(long tgChatId) {
        return dslContext.select(CHAT.fields())
            .from(CHAT)
            .where(CHAT.TG_CHAT_ID.eq(tgChatId))
            .fetchOneInto(Chat.class);
    }

    public List<Chat> getAll() {
        return dslContext.select(CHAT.fields())
            .from(CHAT)
            .fetchInto(Chat.class);
    }

    public void save(Chat chat) {
        dslContext.insertInto(CHAT)
            .set(CHAT.TG_CHAT_ID, chat.getTgChatId())
            .execute();
    }

    public void delete(long id) {
        dslContext.deleteFrom(CHAT)
            .where(CHAT.CHAT_ID.eq(id))
            .execute();
    }

    public void deleteByTgChatId(long tgChatId) {
        dslContext.deleteFrom(CHAT)
            .where(CHAT.TG_CHAT_ID.eq(tgChatId))
            .execute();
    }
}
