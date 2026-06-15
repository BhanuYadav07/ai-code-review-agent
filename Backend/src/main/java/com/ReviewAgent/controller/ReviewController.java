
package com.ReviewAgent.controller;

import com.ReviewAgent.dto.ReviewRequest;
import com.ReviewAgent.dto.ReviewResponse;
import com.ReviewAgent.service.ReviewAgent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ReviewController {

    @Autowired
    private ReviewAgent reviewAgent;

    // POST /api/review  — Submit code for AI review
    @PostMapping("/review")
    public ResponseEntity<?> submitReview(@Valid @RequestBody ReviewRequest request) {
        try {
            ReviewResponse response = reviewAgent.runAgentWorkflow(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Review failed: " + e.getMessage()));
        }
    }

    // GET /api/reviews  — Get all past reviews
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewAgent.getAllReviews());
    }

    // GET /api/reviews/{id}  — Get specific review
    @GetMapping("/reviews/{id}")
    public ResponseEntity<?> getReview(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reviewAgent.getReviewById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/reviews/{id}
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewAgent.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    // Inner error DTO
    record ErrorResponse(String message) {}
}