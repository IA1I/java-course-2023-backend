package edu.java.scrapper.clients.github;

import edu.java.scrapper.dto.RepositoryResponse;

public interface GithubClient {
    RepositoryResponse getRepositoryActivity(String owner, String repo);
}
