package edu.java.scrapper.client.stackoverflow;

import edu.java.scrapper.dto.response.QuestionResponse;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<QuestionResponse> getQuestionActivity(String id);
<<<<<<< Updated upstream
=======

    Mono<CommentResponse> getQuestionComments(String id);

    Mono<AnswerResponse> getQuestionAnswers(String id);
>>>>>>> Stashed changes
}
