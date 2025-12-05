package com.livevote.analyticsconsumer.analyticsconsumer.service;

import com.livevote.analyticsconsumer.analyticsconsumer.dto.VoteDto;
import com.livevote.analyticsconsumer.analyticsconsumer.model.PollMetric;
import com.livevote.analyticsconsumer.analyticsconsumer.model.Vote;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollMetricRepo;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.VoteRepo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
public class VoteCastConsumer implements EventConsumer<VoteDto> {

    private final VoteRepo voteRepository;
    private final PollMetricRepo pollMetricRepository;

    public VoteCastConsumer(VoteRepo voteRepository, PollMetricRepo pollMetricRepository) {
        this.voteRepository = voteRepository;
        this.pollMetricRepository = pollMetricRepository;
    }

    @Override
    @KafkaListener(topics = "vote_cast", groupId = "analytics-service-consumer", containerFactory = "voteCastKafkaListenerContainerFactory")
    @Transactional
    public void consume(VoteDto voteDto) {
        try {
            processVoteCast(voteDto);
        } catch (Exception e) {
            System.out.println("Failed to process PollCreated event");
            throw e;
        }
    }

    public void processVoteCast(VoteDto payload) {

        String voteId = payload.getVoteId();

        // avoid duplicate polls
        if (voteRepository.existsById(voteId)) return;

        Vote vote = buildVote(payload);
        voteRepository.save(vote);

        PollMetric pollMetric = buildPollMetric(payload);
        pollMetricRepository.save(pollMetric);
    }

    private PollMetric buildPollMetric(VoteDto payload) {
        String pollId = payload.getPollId();
        String optionId = payload.getOptionId();
        String metricId = pollId + "-" + optionId;

        return pollMetricRepository.findByPollIdAndOptionId(pollId, optionId)
            .map(m -> {
                m.setTotalVotes(m.getTotalVotes() + 1);
                m.setLastUpdated(Instant.now());
                return m;
            })
            .orElseGet(() -> {
                PollMetric m = new PollMetric();
                m.setId(metricId);
                m.setPollId(pollId);
                m.setOptionId(optionId);
                m.setTotalVotes(1);
                m.setLastUpdated(Instant.now());
                return m;
            });
    }

    private Vote buildVote(VoteDto payload) {
        Vote vote = new Vote();
        vote.setVoteId(payload.getVoteId());
        vote.setPollId(payload.getPollId());
        vote.setOptionId(payload.getOptionId());
        vote.setUserId(payload.getUserId());
        vote.setCreatedAt(payload.getCreatedAt());
        return vote;
    }
}
