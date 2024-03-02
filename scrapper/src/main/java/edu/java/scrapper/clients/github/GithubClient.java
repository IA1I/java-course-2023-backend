package edu.java.scrapper.clients.github;

import edu.java.scrapper.dto.Update;
import java.util.List;
import reactor.core.publisher.Mono;

public interface GithubClient {
    Mono<List<Update>> getRepositoryActivity(String owner, String repo);
}
