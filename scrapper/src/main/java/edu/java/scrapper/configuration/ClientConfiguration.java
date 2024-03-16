package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.bot.DefaultBotClient;
import edu.java.scrapper.client.github.DefaultGithubClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.DefaultStackOverflowClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public GithubClient githubClient(ApplicationConfig applicationConfig) {
        return new DefaultGithubClient(applicationConfig);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(ApplicationConfig applicationConfig) {
        return new DefaultStackOverflowClient(applicationConfig);
    }

    @Bean
    public BotClient botClient(ApplicationConfig applicationConfig) {
        return new DefaultBotClient(applicationConfig);
    }

}
