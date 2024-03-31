package edu.java.bot.configuration;

import edu.java.bot.utils.BackOffPolicy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotEmpty
    String scrapperBaseUrl,
    @NotNull
    BackOffPolicy backOffPolicy,
    @Positive
    Long attempts,
    @NotNull
    Duration duration
) {
}
