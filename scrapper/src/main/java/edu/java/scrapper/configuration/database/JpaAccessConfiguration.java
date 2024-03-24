package edu.java.scrapper.configuration.database;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.repository.jpa.JpaChatRepository;
import edu.java.scrapper.dao.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkUpdater;
import edu.java.scrapper.service.jpa.JpaChatService;
import edu.java.scrapper.service.jpa.JpaLinkService;
import edu.java.scrapper.service.jpa.JpaLinksUpdaterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "jpa")
public class JpaAccessConfiguration {

    @Bean
    public ChatService chatService(JpaChatRepository chatRepository, JpaLinkRepository linkRepository) {
        return new JpaChatService(chatRepository, linkRepository);
    }

    @Bean
    public LinkService linkService(
        JpaChatRepository chatRepository,
        JpaLinkRepository linkRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient
    ) {
        return new JpaLinkService(chatRepository, linkRepository, githubClient, stackOverflowClient);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JpaLinkRepository linkRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        BotClient botClient
    ) {
        return new JpaLinksUpdaterService(linkRepository, githubClient, stackOverflowClient, botClient);
    }
}
