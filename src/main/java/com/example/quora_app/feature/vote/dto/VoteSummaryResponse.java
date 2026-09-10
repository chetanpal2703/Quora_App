package com.example.quora_app.feature.vote.dto;

import com.example.quora_app.feature.vote.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteSummaryResponse {
    private long upvotes;
    private long downvotes;
    private VoteType myVote;
}
