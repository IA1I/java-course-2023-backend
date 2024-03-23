package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateController {

    @PostMapping("/updates")
    public ResponseEntity<String> newUpdate(@RequestBody LinkUpdateRequest updateRequest) {
        return new ResponseEntity<>("Updates received", HttpStatus.OK);
    }
}
