package edu.java.scrapper.configuration;

import edu.java.scrapper.utils.AccessType;
import edu.java.scrapper.utils.BackOffPolicy;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    String githubBaseUrl,
    @NotNull
    String stackOverflowBaseUrl,
    @NotNull
    String botBaseUrl,
    @NotNull
    AccessType accessType,
    @NotNull
    String githubToken,
    @NotNull
    BackOffPolicy backOffPolicy,
    @Positive
    Long attempts,
    @NotNull
    Duration duration
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}
