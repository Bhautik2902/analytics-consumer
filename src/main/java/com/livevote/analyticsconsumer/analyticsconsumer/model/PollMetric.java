package com.livevote.analyticsconsumer.analyticsconsumer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "poll_metrics",
        uniqueConstraints = @UniqueConstraint(columnNames = {"poll_id", "option_id"}))
public class PollMetric {

    @Id
    private String id; // pollId + "-" + optionId

    @Column(name = "poll_id", nullable = false)
    private String pollId;

    @Column(name = "option_id", nullable = false)
    private String optionId;

    @Column(name = "total_votes", nullable = false)
    private int totalVotes = 0;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated = Instant.now();

}
