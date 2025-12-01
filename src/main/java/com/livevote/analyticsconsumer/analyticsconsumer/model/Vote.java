package com.livevote.analyticsconsumer.analyticsconsumer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "votes")
public class Vote {

    @Id
    @Column(name = "vote_id", nullable = false, length = 100)
    private String voteId;

    @Column(name = "poll_id", nullable = false, length = 50)
    private String pollId;

    @Column(name = "option_id", nullable = false, length = 50)
    private String optionId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
