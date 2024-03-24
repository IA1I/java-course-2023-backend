package edu.java.scrapper.dao.repository.jpa;

import edu.java.scrapper.dao.entity.ChatEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<ChatEntity, Long> {
    void deleteByTgChatId(Long tgChatId);

    Optional<ChatEntity> findByTgChatId(Long tgChatId);
}
