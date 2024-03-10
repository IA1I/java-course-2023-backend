package edu.java.scrapper.controller;

import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.ReRegistrationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ChatControllerTest {

    @Autowired
    private ChatController chatController;

    @Test
    void shouldReturnOkResponseEntityForPost() throws ReRegistrationException {
        ResponseEntity<String> expected = new ResponseEntity<>("Chat registered", HttpStatus.CREATED);

        ResponseEntity<String> actual = chatController.addChat(9L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowReRegistrationException() throws ReRegistrationException {

        chatController.addChat(10L);

        assertThrows(ReRegistrationException.class, () -> chatController.addChat(10L));
    }

    @Test
    void shouldReturnOkResponseEntityForDelete() throws ReRegistrationException, ChatIsNotRegisteredException {
        ResponseEntity<String> expected = new ResponseEntity<>("Chat deleted", HttpStatus.OK);

        chatController.addChat(11L);
        ResponseEntity<String> actual = chatController.deleteChat(11L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowException() {
        assertThrows(ChatIsNotRegisteredException.class, () -> chatController.deleteChat(12L));
    }
}
