package com.namhyun.church.question.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Question {
    private Long uniqueId;
    private Integer type;
    private String description;
    private String correctAnswer;
}
