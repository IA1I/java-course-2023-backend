package edu.java.scrapper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionResponse(@JsonProperty("items") List<Item> items) {
    public Item getItem() {
        return items.isEmpty() ? null : items.getFirst();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
        @JsonProperty("is_answered")
        boolean isAnswered,
        @JsonProperty("view_count")
        int viewCount,
        @JsonProperty("answer_count")
        int answerCount,
        @JsonProperty("last_activity_date")
        OffsetDateTime lastActivityDate,
        @JsonProperty("last_edit_date")
        OffsetDateTime lastEditDate
    ) {
    }
}
