package edu.java.scrapper.linkchecker;

import edu.java.scrapper.client.stackoverflow.StackOverflowClient;
import edu.java.scrapper.dao.repository.jdbc.JdbcQuestionRepository;
import edu.java.scrapper.dto.Link;
import edu.java.scrapper.dto.Question;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import edu.java.scrapper.dto.update_mapper.UpdateInfo;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component(value = "stackoverflow.com")
public class StackOverflowChecker extends LinkChecker {
    private static final String SEPARATOR = "/";
    private static final int QUESTION_ID_INDEX = 2;
    private final StackOverflowClient stackOverflowClient;
    private final JdbcQuestionRepository questionRepository;
    private Question question;

    @Autowired
    public StackOverflowChecker(StackOverflowClient stackOverflowClient, JdbcQuestionRepository questionRepository) {
        this.stackOverflowClient = stackOverflowClient;
        this.questionRepository = questionRepository;
    }

    @Override
    public UpdateInfo getUpdateInformation(Link link) {
        URI uri = link.getUri();

        String[] path = uri.getPath().split(SEPARATOR);
        String questionId = path[QUESTION_ID_INDEX];

        Mono<QuestionResponse> questionActivity = stackOverflowClient.getQuestionActivity(questionId);
        QuestionResponse questionResponse = questionActivity.block();
        QuestionResponse.Item itemResponse = questionResponse.getItem();

        Mono<CommentResponse> questionComments = stackOverflowClient.getQuestionComments(questionId);
        CommentResponse commentResponse = questionComments.block();
        question = questionRepository.get(link.getLinkId());

        int newAnswers = itemResponse.answerCount() - question.getAnswersCount();
        int newComments = commentResponse.getCommentsCount() - question.getCommentsCount();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("New answers: ")
            .append(newAnswers)
            .append("\n")
            .append("New comments: ")
            .append(newComments);

        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setUpdatedAt(itemResponse.lastActivityDate());
        updateInfo.setDescription(stringBuilder.toString());

        link.setUpdatedAt(itemResponse.lastActivityDate());
        question.setCommentsCount(commentResponse.getCommentsCount());
        question.setAnswersCount(itemResponse.answerCount());
        questionRepository.update(question);
        return updateInfo;
    }

    @Override
    public void update() {

    }
}
