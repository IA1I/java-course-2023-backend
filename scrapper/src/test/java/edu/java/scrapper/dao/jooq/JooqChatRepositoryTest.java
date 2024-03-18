package edu.java.scrapper.dao.jooq;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dto.Chat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@SpringBootTest
public class JooqChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JooqChatRepository chatRepository;

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

        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        List<Chat> actual = chatRepository.getAll();

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
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        List<Chat> chats = chatRepository.getAll();
        chatRepository.delete(chats.getFirst().getChatId());

        List<Chat> actual = chatRepository.getAll();
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
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        chatRepository.deleteByTgChatId(8L);

        List<Chat> actual = chatRepository.getAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldSaveChat() {
        Chat expected = new Chat();
        expected.setTgChatId(10L);
        chatRepository.save(expected);
        List<Chat> chats = chatRepository.getAll();

        Chat actual = chats.getFirst();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetChatById() {
        Chat expected = new Chat();
        expected.setTgChatId(11L);
        chatRepository.save(expected);
        List<Chat> chats = chatRepository.getAll();

        Chat chat = chats.getFirst();

        Chat actual = chatRepository.get(chat.getChatId());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetChatByTgChatId() {
        Chat expected = new Chat();
        expected.setTgChatId(12L);
        chatRepository.save(expected);

        Chat actual = chatRepository.getByTgChatId(12L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
