package com.example.quora_app.feature.vote.dto;

import com.example.quora_app.feature.vote.enums.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {
    @NotNull(message = "Vote type is required")
    private VoteType type;
}
