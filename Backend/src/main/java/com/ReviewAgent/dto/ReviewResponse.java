package com.ReviewAgent.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewResponse {
    private Long id;
    private String language;

    private Scores scores;
    private Complexity complexity;
    private List<Bug> bugs;
    private List<String> suggestions;
    private String optimizedSnippet;
    private String unitTests;
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scores {
        private int quality;
        private int maintainability;
        private int performance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Complexity {
        private String time;
        private String space;
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bug {
        private String severity;
        private String line;
        private String description;
    }
}