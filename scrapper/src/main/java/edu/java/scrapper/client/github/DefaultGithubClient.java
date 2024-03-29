package edu.java.scrapper.client.github;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.response.UpdateResponse;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
public class DefaultGithubClient implements GithubClient {
    private final WebClient webClient;
    private final String token;

    public DefaultGithubClient(ApplicationConfig applicationConfig) {
        this(applicationConfig.githubBaseUrl(), applicationConfig.githubToken());
    }

    public DefaultGithubClient(String baseUrl, String token) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.token = token;
    }

    @Override
    public Mono<List<UpdateResponse>> getRepositoryActivity(String owner, String repo) {
        return webClient
            .get()
            .uri("/repos/{owner}/{repo}/activity", owner, repo)
            .header("Authorization", token)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<>() {
            });
    }
}
