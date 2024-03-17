package edu.java.scrapper.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnswerResponse(List<Item> items) {

    public int getAnswersCount() {
        return items().size();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record Item(
        Long answerId,
        Long questionId,
        OffsetDateTime lastActivityDate,
        OffsetDateTime lastEditDate
    ) {
    }
}
