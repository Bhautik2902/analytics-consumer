package com.livevote.analyticsconsumer.analyticsconsumer.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @Column(name = "poll_id", nullable = false, length = 50)
    private String pollId;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "pollId", cascade = CascadeType.ALL)
    private List<PollOption> options;
}