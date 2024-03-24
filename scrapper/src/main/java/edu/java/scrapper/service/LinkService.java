package edu.java.scrapper.service;

import edu.java.scrapper.dto.Link;
import java.net.URI;
import java.util.List;

public interface LinkService {

    Link add(long tgChatId, URI uri);

    Link delete(long tgChatId, URI uri);

    List<Link> listAll(long tgChatId);
}
