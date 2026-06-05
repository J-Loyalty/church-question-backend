package com.namhyun.church.question.service;

import com.namhyun.church.question.data.QuizDataLoader;
import com.namhyun.church.question.dto.QuizResponseDto;
import com.namhyun.church.question.entity.Answer;
import com.namhyun.church.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final QuizDataLoader dataLoader;

    public List<Map<String, Object>> getChapterList() {
        Map<Integer, Long> countMap = dataLoader.getQuestions().stream()
                .collect(Collectors.groupingBy(Question::getChapter, Collectors.counting()));

        return countMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("chapter", e.getKey());
                    m.put("name", e.getKey() == 0 ? "서론/총론" : e.getKey() + "장");
                    m.put("count", e.getValue());
                    return m;
                })
                .toList();
    }

    public List<QuizResponseDto> getQuestionsByChapter(int chapter) {
        return dataLoader.getQuestions().stream()
                .filter(q -> q.getChapter() == chapter)
                .map(this::toDto)
                .toList();
    }

    public List<QuizResponseDto> getReviewQuestions(List<Long> questionIds) {
        Set<Long> idSet = new HashSet<>(questionIds);
        return dataLoader.getQuestions().stream()
                .filter(q -> idSet.contains(q.getUniqueId()))
                .map(this::toDto)
                .toList();
    }

    private QuizResponseDto toDto(Question question) {
        QuizResponseDto.QuizResponseDtoBuilder builder = QuizResponseDto.builder()
                .questionId(question.getUniqueId())
                .type(question.getType())
                .description(question.getDescription())
                .correctAnswer(question.getCorrectAnswer());

        if (question.getType() == 2) {
            List<Answer> allAnswers = dataLoader.getAnswersByQuestionId()
                    .getOrDefault(question.getUniqueId(), List.of());
            List<QuizResponseDto.AnswerDto> answerDtos = allAnswers.stream()
                    .map(a -> QuizResponseDto.AnswerDto.builder()
                            .num(a.getNum())
                            .description(a.getDescription())
                            .build())
                    .toList();
            builder.answers(answerDtos);
        }

        return builder.build();
    }
}
