package edu.java.scrapper.controller;

import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.request.AddLinkRequest;
import edu.java.scrapper.dto.request.RemoveLinkRequest;
import edu.java.scrapper.dto.response.LinkResponse;
import edu.java.scrapper.dto.response.ListLinksResponse;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final LinkService linkService;

    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public ResponseEntity<ListLinksResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        List<Link> links = linkService.listAll(chatId);
        List<LinkResponse> linkResponses = new ArrayList<>();
        for (Link link : links) {
            linkResponses.add(new LinkResponse(link));
        }

        return new ResponseEntity<>(new ListLinksResponse(linkResponses, linkResponses.size()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LinkResponse> addLink(
        @RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody AddLinkRequest addLinkRequest
    ) throws URISyntaxException {
        Link link = linkService.add(chatId, new URI(addLinkRequest.link()));

        return new ResponseEntity<>(new LinkResponse(link), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> deleteLink(
        @RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    ) throws URISyntaxException {
        Link link = linkService.delete(chatId, new URI(removeLinkRequest.link()));

        return new ResponseEntity<>(new LinkResponse(link), HttpStatus.OK);
    }

}
