package com.namhyun.church.question.service;

import com.namhyun.church.question.data.QuizDataLoader;
import com.namhyun.church.question.dto.QuizResponseDto;
import com.namhyun.church.question.dto.ScoreRequestDto;
import com.namhyun.church.question.entity.Answer;
import com.namhyun.church.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizDataLoader dataLoader;
    private final List<ScoreRequestDto> scoreStore = new CopyOnWriteArrayList<>();

    public void saveScore(ScoreRequestDto dto) {
        dto.setFinishedAt(LocalDateTime.now());
        scoreStore.add(dto);
    }

    public List<ScoreRequestDto> getScores() {
        return Collections.unmodifiableList(scoreStore);
    }

    public List<ScoreRequestDto> getRanking() {
        return scoreStore.stream()
                .sorted(Comparator.comparingInt(ScoreRequestDto::getScore).reversed()
                        .thenComparingInt(ScoreRequestDto::getElapsed))
                .toList();
    }

    public List<QuizResponseDto> getRandomQuiz(boolean easy) {
        List<Question> all = easy
                ? dataLoader.getQuestions().stream().filter(Question::isEasy).toList()
                : dataLoader.getQuestions();

        List<Question> subjective = new ArrayList<>(all.stream().filter(q -> q.getType() == 1).toList());
        List<Question> objective = new ArrayList<>(all.stream().filter(q -> q.getType() == 2).toList());
        Collections.shuffle(subjective);
        Collections.shuffle(objective);

        List<Question> picked = new ArrayList<>();
        picked.addAll(subjective.stream().limit(4).toList());
        picked.addAll(objective.stream().limit(16).toList());
        Collections.shuffle(picked);

        return picked.stream().map(this::toQuizDto).toList();
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

        Answer correct = allAnswers.stream()
                .filter(a -> a.getDescription().equals(correctAnswer))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "보기에 정답이 없습니다. questionId=" + question.getUniqueId()));

        List<Answer> wrongs = new ArrayList<>(allAnswers.stream()
                .filter(a -> !a.getDescription().equals(correctAnswer))
                .toList());
        Collections.shuffle(wrongs);

        List<Answer> picked = new ArrayList<>();
        picked.add(correct);
        picked.addAll(wrongs.stream().limit(2).toList());
        Collections.shuffle(picked);

        return picked.stream()
                .map(a -> QuizResponseDto.AnswerDto.builder()
                        .num(a.getNum())
                        .description(a.getDescription())
                        .build())
                .toList();
    }
}
