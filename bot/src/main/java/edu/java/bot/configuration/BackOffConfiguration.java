package edu.java.bot.configuration;

import edu.java.bot.exception.ServerException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
@EnableCaching
public class BackOffConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "back-off-policy", havingValue = "constant")
    public Retry retrySpecConstant(ApplicationConfig config) {
        return Retry.max(config.attempts()).filter(throwable -> throwable instanceof ServerException);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "back-off-policy", havingValue = "linear")
    public Retry retrySpecLinear(ApplicationConfig config) {
        return Retry.fixedDelay(config.attempts(), config.duration())
            .filter(throwable -> throwable instanceof ServerException);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "back-off-policy", havingValue = "exponential")
    public Retry retrySpecExponential(ApplicationConfig config) {
        return Retry.backoff(config.attempts(), config.duration())
            .filter(throwable -> throwable instanceof ServerException);
    }
}
