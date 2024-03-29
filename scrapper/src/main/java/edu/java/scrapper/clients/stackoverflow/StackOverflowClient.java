package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.dto.QuestionResponse;
import reactor.core.publisher.Mono;

public interface StackOverflowClient {
    Mono<QuestionResponse> getQuestionActivity(String id);
}
