package edu.java.scrapper.service;

import edu.java.scrapper.dto.request.LinkUpdateRequest;

public interface MessageSender {
    void send(LinkUpdateRequest update);
}
