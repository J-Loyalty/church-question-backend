package com.namhyun.church.question.service;

import com.namhyun.church.question.data.QuizDataLoader;
import com.namhyun.church.question.dto.QuizResponseDto;
import com.namhyun.church.question.entity.Answer;
import com.namhyun.church.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizDataLoader dataLoader;

    public List<QuizResponseDto> getRandomQuiz() {
        List<Question> all = new ArrayList<>(dataLoader.getQuestions());
        Collections.shuffle(all);

        return all.stream().limit(20).map(this::toQuizDto).toList();
    }

    private QuizResponseDto toQuizDto(Question question) {
        QuizResponseDto.QuizResponseDtoBuilder builder = QuizResponseDto.builder()
                .questionId(question.getUniqueId())
                .type(question.getType())
                .description(question.getDescription())
                .correctAnswer(question.getCorrectAnswer());

        if (question.getType() == 2) {
            builder.answers(pickAnswers(question));
        }

        return builder.build();
    }

    private List<QuizResponseDto.AnswerDto> pickAnswers(Question question) {
        List<Answer> allAnswers = dataLoader.getAnswersByQuestionId()
                .getOrDefault(question.getUniqueId(), List.of());

        String correctAnswer = question.getCorrectAnswer();

        // correctAnswer와 일치하는 보기 찾기
        Answer correct = allAnswers.stream()
                .filter(a -> a.getDescription().equals(correctAnswer))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "보기에 정답이 없습니다. questionId=" + question.getUniqueId()));

        // 오답 보기 중 3개 랜덤 선택
        List<Answer> wrongs = new ArrayList<>(allAnswers.stream()
                .filter(a -> !a.getDescription().equals(correctAnswer))
                .toList());
        Collections.shuffle(wrongs);

        // 정답 1개 + 오답 3개 합쳐서 셔플
        List<Answer> picked = new ArrayList<>();
        picked.add(correct);
        picked.addAll(wrongs.stream().limit(3).toList());
        Collections.shuffle(picked);

        return picked.stream()
                .map(a -> QuizResponseDto.AnswerDto.builder()
                        .num(a.getNum())
                        .description(a.getDescription())
                        .build())
                .toList();
    }
}
