package edu.java.scrapper.controller;

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

    @PostMapping("/{id}")
    public ResponseEntity<String> addChat(@PathVariable("id") Long id) {
        return new ResponseEntity<>("Chat registered", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable("id") Long id) {
        return new ResponseEntity<>("Chat deleted", HttpStatus.OK);
    }

}
