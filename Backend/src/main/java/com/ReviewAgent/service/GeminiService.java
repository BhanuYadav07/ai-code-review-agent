package com.ReviewAgent.service;
import com.ReviewAgent.dto.ReviewResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReviewResponse reviewCode(String code, String language) throws Exception {

        String prompt = buildPrompt(code, language);

        Map<String, Object> requestBody = Map.of(
                "contents",
                List.of(
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        String url = apiUrl + "?key=" + apiKey;

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        return parseResponse(response.getBody());
    }

    private String buildPrompt(String code, String language) {
        return """
        You are a code review assistant. Analyze the following %s code and respond ONLY with valid JSON (no markdown fences) in this exact structure:
        {
          "scores": {"quality": <0-100>, "maintainability": <0-100>, "performance": <0-100>},
          "complexity": {"time": "O(...)", "space": "O(...)", "explanation": "..."},
          "bugs": [{"severity": "...", "line": "...", "description": "..."}],
          "suggestions": ["..."],
          "optimizedSnippet": "...",
          "unitTests": "..."
        }

        Code:
        %s
        """.formatted(language, code);
    }

    private ReviewResponse parseResponse(String responseBody) throws Exception {

        JsonNode root = objectMapper.readTree(responseBody);

        String text = root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        text = text.replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode result = objectMapper.readTree(text);

        ReviewResponse.Scores scores = new ReviewResponse.Scores(
                result.path("scores").path("quality").asInt(0),
                result.path("scores").path("maintainability").asInt(0),
                result.path("scores").path("performance").asInt(0)
        );

        ReviewResponse.Complexity complexity = new ReviewResponse.Complexity(
                result.path("complexity").path("time").asText(""),
                result.path("complexity").path("space").asText(""),
                result.path("complexity").path("explanation").asText("")
        );

        List<ReviewResponse.Bug> bugs = objectMapper.convertValue(
                result.path("bugs"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ReviewResponse.Bug.class)
        );

        List<String> suggestions = objectMapper.convertValue(
                result.path("suggestions"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );

        return ReviewResponse.builder()
                .scores(scores)
                .complexity(complexity)
                .bugs(bugs != null ? bugs : List.of())
                .suggestions(suggestions != null ? suggestions : List.of())
                .optimizedSnippet(result.path("optimizedSnippet").asText(""))
                .unitTests(result.path("unitTests").asText(""))
                .build();
    }
}