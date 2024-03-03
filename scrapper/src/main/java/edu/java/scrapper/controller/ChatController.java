package edu.java.scrapper.controller;

import edu.java.scrapper.dao.ChatLocalDao;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.ReRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
public class ChatController {
    private final ChatLocalDao chatLocalDao;

    public ChatController(ChatLocalDao chatLocalDao) {
        this.chatLocalDao = chatLocalDao;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> addChat(@PathVariable("id") long id) throws ReRegistrationException {
        if (chatLocalDao.contains(id)) {
            throw new ReRegistrationException("Chat is already registered");
        }
        chatLocalDao.save(new Chat(id));
        return new ResponseEntity<>("Chat registered", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable("id") long id) throws ChatIsNotRegisteredException {
        if (!chatLocalDao.contains(id)) {
            throw new ChatIsNotRegisteredException("Chat is not registered");
        }
        chatLocalDao.delete(id);
        return new ResponseEntity<>("Chat deleted", HttpStatus.OK);
    }

}
