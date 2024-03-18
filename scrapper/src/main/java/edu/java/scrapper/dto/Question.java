package edu.java.scrapper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Question {
    private long linkId;
    private int commentsCount;
    private int answersCount;
}
