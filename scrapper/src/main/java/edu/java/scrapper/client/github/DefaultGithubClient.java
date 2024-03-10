package edu.java.scrapper.client.github;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.Update;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
public class DefaultGithubClient implements GithubClient {
    private final WebClient webClient;

    public DefaultGithubClient(ApplicationConfig applicationConfig) {
        this(applicationConfig.githubBaseUrl());
    }

    public DefaultGithubClient(String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Override
    public Mono<List<Update>> getRepositoryActivity(String owner, String repo) {
        return webClient
            .get()
            .uri("/repos/{owner}/{repo}/activity", owner, repo)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<>() {
            });
    }
}
