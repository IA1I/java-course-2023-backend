package edu.java.scrapper.client.stackoverflow;

import edu.java.scrapper.dto.response.AnswerResponse;
import edu.java.scrapper.dto.response.CommentResponse;
import edu.java.scrapper.dto.response.QuestionResponse;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<QuestionResponse> getQuestionActivity(String id);

    Mono<CommentResponse> getQuestionComments(String id);

    Mono<AnswerResponse> getQuestionAnswers(String id);

}
