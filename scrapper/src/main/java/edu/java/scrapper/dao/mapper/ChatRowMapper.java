package edu.java.scrapper.dao.mapper;

import edu.java.scrapper.dto.Chat;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ChatRowMapper implements RowMapper<Chat> {
    @Override
    public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
        long chatId = rs.getLong("id");
        long tgChatId = rs.getLong("tg_chat_id");

        return new Chat(chatId, tgChatId);
    }
}
