package edu.java.scrapper.service.jpa;

import edu.java.scrapper.dao.entity.ChatEntity;
import edu.java.scrapper.dao.entity.LinkEntity;
import edu.java.scrapper.dao.repository.jpa.JpaChatRepository;
import edu.java.scrapper.dao.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.ReRegistrationException;
import edu.java.scrapper.service.ChatService;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
public class JpaChatService implements ChatService {
    private final JpaChatRepository chatRepository;
    private final JpaLinkRepository linkRepository;

    public JpaChatService(JpaChatRepository chatRepository, JpaLinkRepository linkRepository) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    @Transactional
    public void register(long tgChatId) {
        ChatEntity chat = new ChatEntity();
        chat.setTgChatId(tgChatId);

        try {
            chatRepository.save(chat);
            log.info("Save chat: {} in DB", tgChatId);
        } catch (DataIntegrityViolationException e) {
            log.info("Chat {} already saved", tgChatId);
            throw new ReRegistrationException("Chat re-registration");
        }
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        Optional<ChatEntity> optionalChat = chatRepository.findByTgChatId(tgChatId);
        if (optionalChat.isEmpty()) {
            log.error("Chat: {} is not registered", tgChatId);
            throw new ChatIsNotRegisteredException("Chat is not registered");
        }

        ChatEntity chat = optionalChat.get();
        for (LinkEntity link : chat.getLinks()) {
            link.getChats().remove(chat);
            linkRepository.save(link);
            log.info("Delete tracked link {} from chat {}", link.getUri(), tgChatId);
        }

        chatRepository.deleteByTgChatId(tgChatId);
        log.info("Delete chat: {} from DB", tgChatId);

        List<LinkEntity> links = linkRepository.findAll();
        List<LinkEntity> trackedLinks = linkRepository.findDistinctTrackedLinks();
        links.removeAll(trackedLinks);

        for (LinkEntity link : links) {
            linkRepository.delete(link);
            log.info("Delete link: {} from DB", link.getUri());
        }
    }
}
