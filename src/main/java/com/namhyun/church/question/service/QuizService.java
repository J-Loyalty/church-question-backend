package com.namhyun.church.question.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.namhyun.church.question.data.QuizDataLoader;
import com.namhyun.church.question.dto.QuizResponseDto;
import com.namhyun.church.question.dto.ScoreRequestDto;
import com.namhyun.church.question.entity.Answer;
import com.namhyun.church.question.entity.Question;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final File SCORE_FILE = new File("scores.json");

    @PostConstruct
    public void loadScores() {
        if (SCORE_FILE.exists()) {
            try {
                List<ScoreRequestDto> loaded = objectMapper.readValue(SCORE_FILE, new TypeReference<>() {});
                scoreStore.addAll(loaded);
            } catch (IOException e) {
                // 파일 읽기 실패 시 빈 상태로 시작
            }
        }
    }

    private void persistScores() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(SCORE_FILE, scoreStore);
        } catch (IOException e) {
            throw new RuntimeException("점수 저장 실패", e);
        }
    }

    public void saveScore(ScoreRequestDto dto) {
        dto.setFinishedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        scoreStore.add(dto);
        persistScores();
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
