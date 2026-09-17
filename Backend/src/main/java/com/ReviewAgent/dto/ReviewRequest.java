package com.ReviewAgent.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

// ── Request ──────────────────────────────────────────────
@Data
@AllArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "Code must not be blank")
    private String code;

    @NotBlank(message = "Language must not be blank")
    private String language;
}