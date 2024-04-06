package edu.java.scrapper.configuration.database;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.repository.jooq.JooqQuestionRepository;
import edu.java.scrapper.dao.repository.jooq.JooqTrackedLinkRepository;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkUpdater;
import edu.java.scrapper.service.jooq.JooqChatService;
import edu.java.scrapper.service.jooq.JooqLinkService;
import edu.java.scrapper.service.jooq.JooqLinksUpdaterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "jooq")
public class JooqAccessConfiguration {

    @Bean
    public ChatService chatService(
        JooqChatRepository chatRepository,
        JooqLinkRepository linkRepository,
        JooqTrackedLinkRepository trackedLinkRepository
    ) {
        return new JooqChatService(chatRepository, linkRepository, trackedLinkRepository);
    }

    @Bean
    public LinkService linkService(
        JooqChatRepository chatRepository,
        JooqLinkRepository linkRepository,
        JooqTrackedLinkRepository trackedLinkRepository,
        JooqQuestionRepository questionRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient
    ) {
        return new JooqLinkService(
            chatRepository,
            linkRepository,
            trackedLinkRepository,
            questionRepository,
            githubClient,
            stackOverflowClient
        );
    }

    @Bean
    public LinkUpdater linkUpdater(
        JooqLinkRepository linkRepository,
        JooqTrackedLinkRepository trackedLinkRepository,
        JooqQuestionRepository questionRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient,
        BotClient botClient
    ) {
        return new JooqLinksUpdaterService(
            linkRepository,
            trackedLinkRepository,
            questionRepository,
            githubClient,
            stackOverflowClient,
            botClient
        );
    }
}
