package edu.java.bot.service;

import edu.java.bot.dto.request.LinkUpdateRequest;

public interface UpdateService {

    void sendUpdate(LinkUpdateRequest updateRequest);
}
