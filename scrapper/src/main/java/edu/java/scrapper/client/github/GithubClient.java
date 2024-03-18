package edu.java.scrapper.client.github;

import edu.java.scrapper.dto.response.UpdateResponse;
import java.util.List;
import reactor.core.publisher.Mono;

public interface GithubClient {
    Mono<List<UpdateResponse>> getRepositoryActivity(String owner, String repo);
}
