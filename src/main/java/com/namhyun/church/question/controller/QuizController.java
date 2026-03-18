package com.namhyun.church.question.controller;

import com.namhyun.church.question.dto.QuizResponseDto;
import com.namhyun.church.question.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public List<QuizResponseDto> getQuiz() {
        return quizService.getRandomQuiz();
    }
}
