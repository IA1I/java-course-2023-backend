package edu.java.scrapper.dto;

import java.net.URI;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Link {
    private long linkId;
    private URI uri;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastCheck;

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Link link = (Link) o;

        return new EqualsBuilder().append(uri, link.uri).append(updatedAt, link.updatedAt)
            .append(lastCheck, link.lastCheck).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(uri).append(updatedAt).append(lastCheck).toHashCode();
    }
}
