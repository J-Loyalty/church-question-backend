package com.namhyun.church.question.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namhyun.church.question.entity.Answer;
import com.namhyun.church.question.entity.Question;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Getter
public class QuizDataLoader {

    private List<Question> questions;
    private Map<Long, List<Answer>> answersByQuestionId;

    @PostConstruct
    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        questions = mapper.readValue(
                new ClassPathResource("questions.json").getInputStream(),
                new TypeReference<>() {});

        List<Answer> answers = mapper.readValue(
                new ClassPathResource("answers.json").getInputStream(),
                new TypeReference<>() {});

        answersByQuestionId = answers.stream()
                .collect(Collectors.groupingBy(Answer::getQuestionId));
    }
}
