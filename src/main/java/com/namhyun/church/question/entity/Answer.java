package com.namhyun.church.question.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Answer {
    private Long uniqueId;
    private Long questionId;
    private Integer num;
    private String description;
}
