package edu.java.bot.controllers;

import edu.java.bot.controller.UpdateController;
import edu.java.bot.dto.request.LinkUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UpdateControllerTest {

    private final UpdateController updateController = new UpdateController();

    @Test
    void shouldReturnOkResponseEntity() {
        ResponseEntity<String> expected = new ResponseEntity<>("Updates received", HttpStatus.OK);
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(0, null, null, null);

        ResponseEntity<String> actual = updateController.newUpdate(linkUpdateRequest);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
