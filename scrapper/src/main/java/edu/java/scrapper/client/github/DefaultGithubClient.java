package edu.java.scrapper.client.github;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.response.UpdateResponse;
import edu.java.scrapper.exception.ServerException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Log4j2
public class DefaultGithubClient implements GithubClient {
    private static final String MESSAGE = "Server error";
    private final WebClient webClient;
    private final String token;
    private final Retry retry;

    public DefaultGithubClient(ApplicationConfig applicationConfig, Retry retry) {
        this(applicationConfig.githubBaseUrl(), applicationConfig.githubToken(), retry);
    }

    public DefaultGithubClient(String baseUrl, String token, Retry retry) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.token = token;
        this.retry = retry;
    }

    @Override
    public Mono<List<UpdateResponse>> getRepositoryActivity(String owner, String repo) {
        return webClient
            .get()
            .uri("/repos/{owner}/{repo}/activity", owner, repo)
            .header("Authorization", token)
            .retrieve()
            .onStatus(
                HttpStatusCode::is5xxServerError,
                clientResponse -> Mono.error(new ServerException(MESSAGE, clientResponse.statusCode().value()))
            )
            .bodyToMono(new ParameterizedTypeReference<List<UpdateResponse>>() {
            })
            .retryWhen(retry);
    }
}
