package edu.java.scrapper.configuration.database;

import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcTrackedLinkRepository;
import edu.java.scrapper.linkchecker.LinkChecker;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkUpdater;
import edu.java.scrapper.service.MessageSender;
import edu.java.scrapper.service.jdbc.JdbcChatService;
import edu.java.scrapper.service.jdbc.JdbcLinkService;
import edu.java.scrapper.service.jdbc.JdbcLinksUpdaterService;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public ChatService chatService(
        JdbcChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        JdbcTrackedLinkRepository trackedLinkRepository
    ) {
        return new JdbcChatService(chatRepository, linkRepository, trackedLinkRepository);
    }

    @Bean
    public LinkService linkService(
        JdbcChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        JdbcTrackedLinkRepository trackedLinkRepository,
        JdbcQuestionRepository questionRepository,
        GithubClient githubClient,
        StackOverflowClient stackOverflowClient
    ) {
        return new JdbcLinkService(
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
        JdbcLinkRepository linkRepository,
        JdbcTrackedLinkRepository trackedLinkRepository,
        MessageSender messageSender,
        Map<String, LinkChecker> checkerMap
    ) {
        return new JdbcLinksUpdaterService(
            linkRepository,
            trackedLinkRepository,
            messageSender,
            checkerMap
        );
    }
}
