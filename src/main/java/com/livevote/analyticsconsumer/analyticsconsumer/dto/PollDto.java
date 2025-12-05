package com.livevote.analyticsconsumer.analyticsconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollDto {

    @JsonProperty("poll_id")
    private String pollId;

    private String question;

    private String createdBy;

    private Instant createdAt;

    private Long durationMin;

    private List<PollOptionDto> options;
}
