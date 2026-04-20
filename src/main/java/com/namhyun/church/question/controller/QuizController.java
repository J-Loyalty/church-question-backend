package com.namhyun.church.question.controller;

import com.namhyun.church.question.dto.QuizResponseDto;
import com.namhyun.church.question.dto.ScoreRequestDto;
import com.namhyun.church.question.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/score")
    public void saveScore(@RequestBody ScoreRequestDto dto) {
        quizService.saveScore(dto);
    }

    @GetMapping("/score")
    public List<ScoreRequestDto> getScores() {
        return quizService.getScores();
    }

    @GetMapping("/ranking")
    public List<ScoreRequestDto> getRanking() {
        return quizService.getRanking();
    }
}
