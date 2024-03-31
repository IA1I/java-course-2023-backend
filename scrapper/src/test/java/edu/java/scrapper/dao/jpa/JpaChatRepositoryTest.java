package edu.java.scrapper.dao.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.entity.ChatEntity;
import edu.java.scrapper.dao.repository.jpa.JpaChatRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "app.access-type=jpa")
public class JpaChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JpaChatRepository chatRepository;

    @Test
    @Transactional
    @Rollback
    void shouldReturnListOfChatsFromDB() {
        ChatEntity chat1 = new ChatEntity();
        ChatEntity chat2 = new ChatEntity();
        ChatEntity chat3 = new ChatEntity();
        chat1.setTgChatId(1L);
        chat2.setTgChatId(2L);
        chat3.setTgChatId(3L);
        List<ChatEntity> expected = List.of(chat1, chat2, chat3);

        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        List<ChatEntity> actual = chatRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteChatAndReturnListOfChatsFromDB() {
        ChatEntity chat1 = new ChatEntity();
        ChatEntity chat2 = new ChatEntity();
        ChatEntity chat3 = new ChatEntity();
        chat1.setTgChatId(4L);
        chat2.setTgChatId(5L);
        chat3.setTgChatId(6L);
        List<ChatEntity> expected = List.of(chat2, chat3);
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        List<ChatEntity> chats = chatRepository.findAll();
        chatRepository.delete(chats.getFirst());

        List<ChatEntity> actual = chatRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldDeleteChatByTgChatIdAndReturnListOfChatsFromDB() {
        ChatEntity chat1 = new ChatEntity();
        ChatEntity chat2 = new ChatEntity();
        ChatEntity chat3 = new ChatEntity();
        chat1.setTgChatId(7L);
        chat2.setTgChatId(8L);
        chat3.setTgChatId(9L);
        List<ChatEntity> expected = List.of(chat1, chat3);
        chatRepository.save(chat1);
        chatRepository.save(chat2);
        chatRepository.save(chat3);

        chatRepository.deleteByTgChatId(8L);

        List<ChatEntity> actual = chatRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldSaveChat() {
        ChatEntity expected = new ChatEntity();
        expected.setTgChatId(10L);
        chatRepository.save(expected);
        List<ChatEntity> chats = chatRepository.findAll();

        ChatEntity actual = chats.getFirst();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetChatById() {
        ChatEntity expected = new ChatEntity();
        expected.setTgChatId(11L);
        chatRepository.save(expected);
        List<ChatEntity> chats = chatRepository.findAll();

        ChatEntity chat = chats.getFirst();

        Optional<ChatEntity> actual = chatRepository.findById(chat.getId());

        Assertions.assertThat(actual.orElse(null)).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetChatByTgChatId() {
        ChatEntity expected = new ChatEntity();
        expected.setTgChatId(12L);
        chatRepository.save(expected);

        Optional<ChatEntity> actual = chatRepository.findByTgChatId(12L);

        Assertions.assertThat(actual.orElse(null)).isEqualTo(expected);
    }
}
