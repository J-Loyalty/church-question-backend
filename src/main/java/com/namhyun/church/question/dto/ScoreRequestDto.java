package com.namhyun.church.question.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScoreRequestDto {
    private String name;
    private int score;
    private int total;
    private int elapsed;
    private LocalDateTime finishedAt;
    private List<Detail> details;

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Getter
    @NoArgsConstructor
    public static class Detail {
        private String question;
        private String userAnswer;
        private String correctAnswer;
        private boolean correct;
    }
}
