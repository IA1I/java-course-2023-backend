package edu.java.scrapper.dto.response;

import java.util.Objects;

public record LinkResponse(
    Long id,
    String url
) {
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
