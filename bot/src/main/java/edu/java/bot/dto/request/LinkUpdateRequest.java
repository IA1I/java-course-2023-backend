package edu.java.bot.dto.request;

public record LinkUpdateRequest(
    long id,
    String url,
    String description,
    long[] tgChatIds
) {
}
