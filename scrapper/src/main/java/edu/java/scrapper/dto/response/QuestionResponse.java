package edu.java.scrapper.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionResponse(List<Item> items) {
    public Item getItem() {
        return items.isEmpty() ? null : items.getFirst();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record Item(
        Boolean isAnswered,
        Integer viewCount,
        Integer answerCount,
        OffsetDateTime lastActivityDate,
        OffsetDateTime lastEditDate
    ) {
    }
}
