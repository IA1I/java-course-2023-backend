package edu.java.scrapper.clients.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.scrapper.dto.RepositoryResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
public class DefaultGithubClient implements GithubClient {
    private static final String BASE_URL = "https://api.github.com";
    private final WebClient webClient;
    private final ObjectMapper mapper;

    public DefaultGithubClient() {
        this(BASE_URL);
    }

    public DefaultGithubClient(String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
        this.mapper = new ObjectMapper();
        setupObjectMapper();
    }

    @Override
    public RepositoryResponse getRepositoryActivity(String owner, String repo) {
        String response = webClient
            .get()
            .uri("/repos/{owner}/{repo}/activity", owner, repo)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return fetchResponse(response);
    }

    private RepositoryResponse fetchResponse(String response) {
        List<RepositoryResponse.Update> updates = null;
        try {
            updates = mapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            updates = new ArrayList<>();
            log.error("Exception JSON parsing {}", e.getMessage());
        }

        return new RepositoryResponse(updates);
    }

    private void setupObjectMapper() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
