package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.DefaultBotClient;
import edu.java.scrapper.client.github.DefaultGithubClient;
import edu.java.scrapper.client.stackoverflow.DefaultStackOverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public DefaultGithubClient githubClient(ApplicationConfig applicationConfig) {
        return new DefaultGithubClient(applicationConfig);
    }

    @Bean
    public DefaultStackOverflowClient stackOverflowClient(ApplicationConfig applicationConfig) {
        return new DefaultStackOverflowClient(applicationConfig);
    }

    @Bean
    public BotClient botClient(ApplicationConfig applicationConfig) {
        return new DefaultBotClient(applicationConfig);
    }

}
