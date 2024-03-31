package edu.java.scrapper.linkchecker;

import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.response.UpdateResponse;
import edu.java.scrapper.dto.update_mapper.UpdateInfo;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component(value = "github.com")
public class GithubLinkChecker extends LinkChecker {
    private static final String SEPARATOR = "/";
    private static final int REPOSITORY_OWNER_INDEX = 1;
    private static final int REPOSITORY_NAME_INDEX = 2;
    private final GithubClient githubClient;

    @Autowired
    public GithubLinkChecker(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public UpdateInfo getUpdateInformation(Link link) {
        URI uri = link.getUri();

        String[] path = uri.getPath().split(SEPARATOR);
        String owner = path[REPOSITORY_OWNER_INDEX];
        String repo = path[REPOSITORY_NAME_INDEX];

        Mono<List<UpdateResponse>> repositoryActivity = githubClient.getRepositoryActivity(owner, repo);
        UpdateResponse response = repositoryActivity.block().getFirst();

        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setUpdatedAt(response.timestamp());
        updateInfo.setDescription("Activity type: " + response.activityType());

        link.setUpdatedAt(response.timestamp());
        return updateInfo;
    }

    @Override
    public void update() {

    }
}
