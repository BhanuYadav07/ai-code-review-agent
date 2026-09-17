
package com.ReviewAgent.service;
import com.ReviewAgent.Entity.CodeReview;
import com.ReviewAgent.Repository.CodeReviewRepository;
import com.ReviewAgent.dto.ReviewRequest;
import com.ReviewAgent.dto.ReviewResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewAgent {

    @Autowired
    private GroqService groqService;

    @Autowired
    private CodeReviewRepository codeReviewRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReviewResponse runAgentWorkflow(ReviewRequest request) throws Exception {

        ReviewResponse response = groqService.reviewCode(
                request.getCode(),
                request.getLanguage()
        );

        // Persist to MySQL
        CodeReview entity = new CodeReview();
        entity.setCode(request.getCode());
        entity.setLanguage(request.getLanguage());
        entity.setQualityScore(response.getScores().getQuality());
        entity.setMaintainabilityScore(response.getScores().getMaintainability());
        entity.setPerformanceScore(response.getScores().getPerformance());
        entity.setTimeComplexity(response.getComplexity().getTime());
        entity.setSpaceComplexity(response.getComplexity().getSpace());
        entity.setComplexityExplanation(response.getComplexity().getExplanation());
        entity.setBugs(objectMapper.writeValueAsString(response.getBugs()));
        entity.setSuggestions(objectMapper.writeValueAsString(response.getSuggestions()));
        entity.setOptimizedSnippet(response.getOptimizedSnippet());
        entity.setUnitTests(response.getUnitTests());

        CodeReview saved = codeReviewRepository.save(entity);
        response.setId(saved.getId());
        response.setCreatedAt(saved.getCreatedAt());
        response.setLanguage(request.getLanguage());

        return response;
    }

    // ── Get all reviews ──
    public List<ReviewResponse> getAllReviews() {
        return codeReviewRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Get by ID ──
    public ReviewResponse getReviewById(Long id) throws Exception {
        CodeReview entity = codeReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        return mapToResponse(entity);
    }

    // ── Delete by ID ──
    public void deleteReview(Long id) {
        codeReviewRepository.deleteById(id);
    }

    // ── Entity → DTO ──
    private ReviewResponse mapToResponse(CodeReview entity) {
        try {
            ReviewResponse.Scores scores = new ReviewResponse.Scores(
                    entity.getQualityScore() != null ? entity.getQualityScore() : 0,
                    entity.getMaintainabilityScore() != null ? entity.getMaintainabilityScore() : 0,
                    entity.getPerformanceScore() != null ? entity.getPerformanceScore() : 0
            );

            ReviewResponse.Complexity complexity = new ReviewResponse.Complexity(
                    entity.getTimeComplexity(),
                    entity.getSpaceComplexity(),
                    entity.getComplexityExplanation()
            );

            List<ReviewResponse.Bug> bugs = objectMapper.readValue(
                    entity.getBugs() != null ? entity.getBugs() : "[]",
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ReviewResponse.Bug.class)
            );

            List<String> suggestions = objectMapper.readValue(
                    entity.getSuggestions() != null ? entity.getSuggestions() : "[]",
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            return ReviewResponse.builder()
                    .id(entity.getId())
                    .language(entity.getLanguage())
                    .scores(scores)
                    .complexity(complexity)
                    .bugs(bugs)
                    .suggestions(suggestions)
                    .optimizedSnippet(entity.getOptimizedSnippet())
                    .unitTests(entity.getUnitTests())
                    .createdAt(entity.getCreatedAt())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error mapping review entity to response", e);
        }
    }
}