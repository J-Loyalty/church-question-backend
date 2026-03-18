package com.namhyun.church.question.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class QuizResponseDto {
    private Long questionId;
    private Integer type;
    private String description;
    private String correctAnswer;
    private List<AnswerDto> answers; // 주관식이면 null

    @Getter
    @Builder
    public static class AnswerDto {
        private Integer num;
        private String description;
    }
}
