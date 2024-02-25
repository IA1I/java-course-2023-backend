package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.dto.QuestionResponse;

public interface StackOverflowClient {
    QuestionResponse getQuestionActivity(String id);
}
