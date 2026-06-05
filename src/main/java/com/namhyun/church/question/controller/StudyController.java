package com.namhyun.church.question.controller;

import com.namhyun.church.question.dto.QuizResponseDto;
import com.namhyun.church.question.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/chapters")
    public List<Map<String, Object>> getChapters() {
        return studyService.getChapterList();
    }

    @GetMapping("/chapter/{chapter}")
    public List<QuizResponseDto> getByChapter(@PathVariable int chapter) {
        return studyService.getQuestionsByChapter(chapter);
    }

    @GetMapping("/review")
    public List<QuizResponseDto> getReview(@RequestParam List<Long> questionIds) {
        return studyService.getReviewQuestions(questionIds);
    }
}
