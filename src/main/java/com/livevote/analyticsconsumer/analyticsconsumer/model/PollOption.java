package com.livevote.analyticsconsumer.analyticsconsumer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "poll_options")
public class PollOption {

    @Id
    @Column(name = "option_id", nullable = false, length = 50)
    private String optionId;

    @Column(name = "poll_id", nullable = false, length = 50)
    private String pollId;

    @Column(name = "text", nullable = false)
    private String text;
}
