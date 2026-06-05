package com.namhyun.church.question.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@EnableScheduling
public class KeepAlive {

    @Value("${app.base-url:}")
    private String baseUrl;

    private final HttpClient client = HttpClient.newHttpClient();

    @Scheduled(fixedRate = 600000) // 10분마다
    public void ping() {
        if (baseUrl == null || baseUrl.isBlank()) return;
        try {
            client.send(
                HttpRequest.newBuilder().uri(URI.create(baseUrl + "/api/quiz")).GET().build(),
                HttpResponse.BodyHandlers.discarding()
            );
        } catch (Exception ignored) {}
    }
}
