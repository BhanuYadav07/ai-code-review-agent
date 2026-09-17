package com.ReviewAgent.service;

import com.ReviewAgent.dto.ReviewResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReviewResponse reviewCode(String code, String language) throws Exception {

        String prompt = buildPrompt(code, language);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0.2
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        apiUrl,
                        entity,
                        String.class
                );

        return parseResponse(response.getBody());
    }

    private String buildPrompt(String code, String language) {

        return """
        You are an expert code review assistant.

        Analyze the following %s code.

        Respond ONLY with valid JSON.
        Do NOT use markdown.
        Do NOT wrap the response in ```json.
        Do NOT add any explanation outside the JSON.

        Return exactly this structure:

        {
          "scores": {
            "quality": 0,
            "maintainability": 0,
            "performance": 0
          },
          "complexity": {
            "time": "O(...)",
            "space": "O(...)",
            "explanation": "..."
          },
          "bugs": [
            {
              "severity": "...",
              "line": "...",
              "description": "..."
            }
          ],
          "suggestions": [
            "..."
          ],
          "optimizedSnippet": "...",
          "unitTests": "..."
        }

        Rules:
        - Scores must be integers from 0 to 100.
        - Identify actual bugs rather than inventing them.
        - Give accurate time and space complexity.
        - Provide useful optimization suggestions.
        - optimizedSnippet should contain improved code.
        - unitTests should contain relevant tests.
        - If there are no bugs, return an empty bugs array.

        Code:

        %s
        """.formatted(language, code);
    }

    private ReviewResponse parseResponse(String responseBody) throws Exception {

        JsonNode root = objectMapper.readTree(responseBody);

        /*
         * Groq response:
         *
         * {
         *   "choices": [
         *     {
         *       "message": {
         *         "content": "..."
         *       }
         *     }
         *   ]
         * }
         */

        JsonNode choices = root.path("choices");

        if (!choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException(
                    "Groq returned no choices: " + responseBody
            );
        }

        String text = choices
                .get(0)
                .path("message")
                .path("content")
                .asText();

        if (text == null || text.isBlank()) {
            throw new RuntimeException(
                    "Groq returned empty response"
            );
        }

        // Remove markdown fences if model accidentally adds them
        text = text
                .replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode result = objectMapper.readTree(text);

        ReviewResponse.Scores scores =
                new ReviewResponse.Scores(
                        result.path("scores")
                                .path("quality")
                                .asInt(0),

                        result.path("scores")
                                .path("maintainability")
                                .asInt(0),

                        result.path("scores")
                                .path("performance")
                                .asInt(0)
                );

        ReviewResponse.Complexity complexity =
                new ReviewResponse.Complexity(
                        result.path("complexity")
                                .path("time")
                                .asText(""),

                        result.path("complexity")
                                .path("space")
                                .asText(""),

                        result.path("complexity")
                                .path("explanation")
                                .asText("")
                );

        List<ReviewResponse.Bug> bugs =
                objectMapper.convertValue(
                        result.path("bugs"),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(
                                        List.class,
                                        ReviewResponse.Bug.class
                                )
                );

        List<String> suggestions =
                objectMapper.convertValue(
                        result.path("suggestions"),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(
                                        List.class,
                                        String.class
                                )
                );

        return ReviewResponse.builder()
                .scores(scores)
                .complexity(complexity)
                .bugs(bugs != null ? bugs : List.of())
                .suggestions(
                        suggestions != null
                                ? suggestions
                                : List.of()
                )
                .optimizedSnippet(
                        result.path("optimizedSnippet")
                                .asText("")
                )
                .unitTests(
                        result.path("unitTests")
                                .asText("")
                )
                .build();
    }
}