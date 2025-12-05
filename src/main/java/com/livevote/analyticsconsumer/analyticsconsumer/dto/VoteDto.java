package com.livevote.analyticsconsumer.analyticsconsumer.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDto {

    private String voteId;

    private String pollId;

    private String optionId;

    private String userId;

    private Instant createdAt;
}
