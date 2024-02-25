package edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record RepositoryResponse(List<Update> updates) {
    public Update getLastUpdate() {
        Optional<Update> optional = updates.stream().max(Comparator.comparing(Update::timestamp));

        return optional.orElse(null);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Update(
        @JsonProperty("id")
        Long id,
        @JsonProperty("timestamp")
        OffsetDateTime timestamp
    ) {
    }
}

