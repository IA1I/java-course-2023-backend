package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.client.github.GithubClient;
import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.dto.response.UpdateResponse;
import java.net.URI;
import java.util.List;
import reactor.core.publisher.Mono;

public final class JdbcUtils {
    private static final String SEPARATOR = "/";
    private static final String DESCRIPTION = "Link updated";
    private static final int QUESTION_ID_INDEX = 2;
    private static final long CHECK_TIME = 3L;
    private static final int REPOSITORY_OWNER_INDEX = 1;
    private static final int REPOSITORY_NAME_INDEX = 2;

    private JdbcUtils() {
    }

    public static UpdateResponse getUpdate(URI uri, GithubClient githubClient) {
        String[] path = uri.getPath().split(SEPARATOR);
        String owner = path[REPOSITORY_OWNER_INDEX];
        String repo = path[REPOSITORY_NAME_INDEX];

        Mono<List<UpdateResponse>> repositoryActivity = githubClient.getRepositoryActivity(owner, repo);

        return repositoryActivity.block().getFirst();
    }

    public static QuestionResponse getQuestionResponse(URI uri, StackOverflowClient stackOverflowClient) {
        String questionId = getQuestionId(uri);

        Mono<QuestionResponse> questionActivity = stackOverflowClient.getQuestionActivity(questionId);

        return questionActivity.block();
    }

    public static CommentResponse getCommentResponse(URI uri, StackOverflowClient stackOverflowClient) {
        String questionId = getQuestionId(uri);

        Mono<CommentResponse> questionActivity = stackOverflowClient.getQuestionComments(questionId);

        return questionActivity.block();
    }

    private static String getQuestionId(URI uri) {
        String[] path = uri.getPath().split(SEPARATOR);

        return path[QUESTION_ID_INDEX];
    }
}
