package edu.java.scrapper.dto;

import edu.java.scrapper.dto.response.LinkResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Chat {
    private long linkId;

    private final long id;
    private final List<LinkResponse> links;

    public Chat(long id) {
        this.id = id;
        this.links = new ArrayList<>();
        this.linkId = 0L;
    }

    public LinkResponse addLink(String uri) {
        LinkResponse link = new LinkResponse(linkId++, uri);
        links.add(link);

        return link;
    }

    public LinkResponse removeLink(String uri) {
        LinkResponse link = null;
        for (LinkResponse linkResponse : links) {
            if (linkResponse.url().equals(uri)) {
                link = linkResponse;
                break;
            }
        }
        links.remove(link);

        return link;
    }

    public boolean containsLink(String uri) {
        for (LinkResponse link : links) {
            if (link.url().equals(uri)) {
                return true;
            }
        }

        return false;
    }
}
