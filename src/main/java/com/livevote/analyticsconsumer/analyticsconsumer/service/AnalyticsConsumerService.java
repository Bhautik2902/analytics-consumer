package com.livevote.analyticsconsumer.analyticsconsumer.service;

import com.livevote.analyticsconsumer.analyticsconsumer.dto.*;
import com.livevote.analyticsconsumer.analyticsconsumer.model.Poll;
import com.livevote.analyticsconsumer.analyticsconsumer.model.PollMetric;
import com.livevote.analyticsconsumer.analyticsconsumer.model.PollOption;
import com.livevote.analyticsconsumer.analyticsconsumer.model.Vote;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollMetricRepo;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollOptionRepo;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollRepo;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.VoteRepo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class AnalyticsConsumerService {

    private final PollRepo pollRepository;
    private final PollOptionRepo pollOptionRepository;
    private final VoteRepo voteRepository;
    private final PollMetricRepo pollMetricRepo;


    public AnalyticsConsumerService(PollRepo pollRepository, PollOptionRepo pollOptionRepository, VoteRepo voteRepository, PollMetricRepo pollMetricRepo) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.voteRepository = voteRepository;
        this.pollMetricRepo = pollMetricRepo;
    }

    @KafkaListener(topics = "poll_created", groupId = "analytics-service-consumer")
    @Transactional
    public void consumePollCreated(EventWrapper wrapper) {

        try {
            if (wrapper == null || wrapper.getPayload() == null) {
                System.out.println("Wrapper or payload missing");
                return;
            }

            LinkedHashMap<String, Object> payload = wrapper.getPayload();

            String pollId = (String) payload.get("poll_id");
            if (pollId == null) {
                System.out.println("pollId missing.");
                return;
            }

            // Skip duplicate polls
            if (pollRepository.existsById(pollId)) {
                System.out.println("Poll already exists, skipping pollId: " + pollId);
                return;
            }

            Instant createdAt = Instant.ofEpochMilli((Long) payload.get("createdAt"));
            Instant expiresAt = createdAt.plus(Duration.ofMinutes((Integer) payload.get("durationMin")));

            // Create poll record
            Poll poll = new Poll();
            poll.setPollId(pollId);
            poll.setQuestion((String) payload.get("question"));
            poll.setCreatedBy((String) payload.get("createdBy"));
            poll.setCreatedAt(createdAt);
            poll.setExpiresAt(expiresAt);
            poll.setStatus("OPEN");

            pollRepository.save(poll);

            // Create poll options
            List<LinkedHashMap<String, Object>> options = (List<LinkedHashMap<String, Object>>) payload.get("options");
            if (options != null) {
                for (LinkedHashMap<String, Object> o : options) {
                    String optionId = (String) o.get("optionId");

                    if (pollOptionRepository.existsById(optionId)) {
                        System.out.println("Option exists, skipping optionId=" + optionId);
                        continue;
                    }

                    PollOption opt = new PollOption();
                    opt.setOptionId(optionId);
                    opt.setPollId(pollId);
                    opt.setText((String) o.get("text"));

                    pollOptionRepository.save(opt);
                }
            }
            System.out.println("Saved poll + options for pollId=" + pollId);

        } catch (Exception e) {
            System.err.println("Error processing PollCreated event: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "vote_cast", groupId = "analytics-service-consumer")
    @Transactional
    public void consumeVoteCast(EventWrapper wrapper) {

        try {
            if (wrapper == null || wrapper.getPayload() == null) {
                System.out.println("Payload missing, skipping...");
                return;
            }

            LinkedHashMap<String, Object> voteDto = wrapper.getPayload();

            Vote vote = new Vote();
            vote.setVoteId((String) voteDto.get("voteId"));
            vote.setPollId((String) voteDto.get("pollId"));
            vote.setOptionId((String) voteDto.get("optionId"));
            vote.setUserId((String) voteDto.get("userId"));

            Object createdAtObj = voteDto.get("createdAt");
            if (createdAtObj != null) {
                vote.setCreatedAt(Instant.ofEpochMilli(((Number) createdAtObj).longValue()));
            } else {
                vote.setCreatedAt(Instant.now());
            }
            voteRepository.save(vote);


            // updating the matric table
            String pollId = (String) voteDto.get("pollId");
            String optionId = (String) voteDto.get("optionId");
            String metricId = pollId + "-" + optionId;

            PollMetric metric = pollMetricRepo.findByPollIdAndOptionId(pollId, optionId)
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
            pollMetricRepo.save(metric);

        }
        catch (Exception e) {
            System.err.println("Error processing PollCreated event: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
