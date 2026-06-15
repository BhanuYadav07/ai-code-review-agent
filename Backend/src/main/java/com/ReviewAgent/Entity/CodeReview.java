
package com.ReviewAgent.Entity;
import jakarta.persistence.*;
        import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(name = "quality_score")
    private Integer qualityScore;

    @Column(name = "maintainability_score")
    private Integer maintainabilityScore;

    @Column(name = "performance_score")
    private Integer performanceScore;

    @Column(name = "time_complexity", length = 50)
    private String timeComplexity;

    @Column(name = "space_complexity", length = 50)
    private String spaceComplexity;

    @Column(name = "complexity_explanation", columnDefinition = "TEXT")
    private String complexityExplanation;

    @Column(columnDefinition = "JSON")
    private String bugs;

    @Column(columnDefinition = "JSON")
    private String suggestions;

    @Column(name = "optimized_snippet", columnDefinition = "TEXT")
    private String optimizedSnippet;

    @Column(name = "unit_tests", columnDefinition = "TEXT")
    private String unitTests;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}