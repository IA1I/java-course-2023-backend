package edu.java.scrapper.dao.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dto.Chat;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class JdbcChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcChatRepository chatDao;

    @Test
    @Transactional
    @Rollback
    void shouldReturnListOfChatsFromDB() {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        Chat chat3 = new Chat();
        chat1.setTgChatId(1L);
        chat2.setTgChatId(2L);
        chat3.setTgChatId(3L);
        List<Chat> expected = List.of(chat1, chat2, chat3);

        chatDao.save(chat1);
        chatDao.save(chat2);
        chatDao.save(chat3);

        List<Chat> actual = chatDao.getAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteChatAndReturnListOfChatsFromDB() {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        Chat chat3 = new Chat();
        chat1.setTgChatId(4L);
        chat2.setTgChatId(5L);
        chat3.setTgChatId(6L);
        List<Chat> expected = List.of(chat2, chat3);
        chatDao.save(chat1);
        chatDao.save(chat2);
        chatDao.save(chat3);

        List<Chat> chats = chatDao.getAll();
        chatDao.delete(chats.getFirst().getChatId());

        List<Chat> actual = chatDao.getAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteChatByTgChatIdAndReturnListOfChatsFromDB() {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        Chat chat3 = new Chat();
        chat1.setTgChatId(7L);
        chat2.setTgChatId(8L);
        chat3.setTgChatId(9L);
        List<Chat> expected = List.of(chat1, chat3);
        chatDao.save(chat1);
        chatDao.save(chat2);
        chatDao.save(chat3);

        chatDao.deleteByTgChatId(8L);

        List<Chat> actual = chatDao.getAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldSaveChat(){
        Chat expected = new Chat();
        expected.setTgChatId(10L);
        chatDao.save(expected);
        List<Chat> chats = chatDao.getAll();

        Chat actual = chats.getFirst();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetChatById(){
        Chat expected = new Chat();
        expected.setTgChatId(11L);
        chatDao.save(expected);
        List<Chat> chats = chatDao.getAll();

        Chat chat = chats.getFirst();

        Chat actual = chatDao.get(chat.getChatId());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetChatByTgChatId(){
        Chat expected = new Chat();
        expected.setTgChatId(12L);
        chatDao.save(expected);

        Chat actual = chatDao.getByTgChatId(12L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
