package com.ReviewAgent.Repository;

import com.ReviewAgent.Entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {
    List<CodeReview> findAllByOrderByCreatedAtDesc();
    List<CodeReview> findByLanguageOrderByCreatedAtDesc(String language);
}
