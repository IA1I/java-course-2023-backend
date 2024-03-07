package edu.java.scrapper.configuration;

import edu.java.scrapper.clients.github.DefaultGithubClient;
import edu.java.scrapper.clients.stackoverflow.DefaultStackOverflowClient;
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
}
