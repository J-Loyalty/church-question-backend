package com.namhyun.church.question.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScoreRequestDto {
    private String name;
    private int score;
    private int total;
    private int elapsed;
    private List<Detail> details;

    @Getter
    @NoArgsConstructor
    public static class Detail {
        private String question;
        private String userAnswer;
        private String correctAnswer;
        private boolean correct;
    }
}
