package edu.java.scrapper.dto.response;

import edu.java.scrapper.dto.Link;
import java.util.Objects;

public record LinkResponse(
    Long id,
    String url
) {
    public LinkResponse(Link link) {
        this(link.getLinkId(), link.getUri().toString());
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkResponse that = (LinkResponse) o;

        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
