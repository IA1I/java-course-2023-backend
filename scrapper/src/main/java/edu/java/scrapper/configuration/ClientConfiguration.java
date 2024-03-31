package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.DefaultBotClient;
import edu.java.scrapper.client.github.DefaultGithubClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.DefaultStackOverflowClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
public class ClientConfiguration {

    @Bean
    public GithubClient githubClient(ApplicationConfig applicationConfig, Retry retry) {
        return new DefaultGithubClient(applicationConfig, retry);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(ApplicationConfig applicationConfig, Retry retry) {
        return new DefaultStackOverflowClient(applicationConfig, retry);
    }

    @Bean
    public BotClient botClient(ApplicationConfig applicationConfig, Retry retry) {
        return new DefaultBotClient(applicationConfig, retry);
    }

}
