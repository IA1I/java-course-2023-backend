package edu.java.scrapper.controller;

import edu.java.scrapper.dao.ChatLocalDao;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.request.AddLinkRequest;
import edu.java.scrapper.dto.request.RemoveLinkRequest;
import edu.java.scrapper.dto.response.LinkResponse;
import edu.java.scrapper.dto.response.ListLinksResponse;
import edu.java.scrapper.exception.ChatIsNotRegisteredException;
import edu.java.scrapper.exception.LinkIsNotTrackedException;
import edu.java.scrapper.exception.ReAddLinkException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class LinkController {
    private final ChatLocalDao chatLocalDao;

    public LinkController(ChatLocalDao chatLocalDao) {
        this.chatLocalDao = chatLocalDao;
    }

    @GetMapping
    public ResponseEntity<ListLinksResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") long chatId)
        throws ChatIsNotRegisteredException {
        isChatExists(chatId);
        List<LinkResponse> links = chatLocalDao.get(chatId).getLinks();

        return new ResponseEntity<>(new ListLinksResponse(links, links.size()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LinkResponse> addLink(
        @RequestHeader("Tg-Chat-Id") long chatId,
        @RequestBody AddLinkRequest addLinkRequest
    ) throws ChatIsNotRegisteredException, ReAddLinkException {
        isChatExists(chatId);
        Chat chat = chatLocalDao.get(chatId);
        if (chat.containsLink(addLinkRequest.link())) {
            throw new ReAddLinkException("Link is already being tracked");
        }

        return new ResponseEntity<>(chat.addLink(addLinkRequest.link()), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> deleteLink(
        @RequestHeader("Tg-Chat-Id") long chatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    ) throws ChatIsNotRegisteredException, LinkIsNotTrackedException {
        isChatExists(chatId);
        Chat chat = chatLocalDao.get(chatId);
        if (!chat.containsLink(removeLinkRequest.link())) {
            throw new LinkIsNotTrackedException("Link is not tracked");
        }

        return new ResponseEntity<>(chat.removeLink(removeLinkRequest.link()), HttpStatus.OK);
    }

    private void isChatExists(long chatId) throws ChatIsNotRegisteredException {
        if (!chatLocalDao.contains(chatId)) {
            throw new ChatIsNotRegisteredException("Chat is not registered");
        }
    }
}
