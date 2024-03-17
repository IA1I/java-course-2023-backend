package edu.java.scrapper.dto.request;

public record LinkUpdateRequest(
    Long id,
    String url,
    String description,
    Long[] tgChatIds
) {
}
