package com.livevote.analyticsconsumer.analyticsconsumer.service;

import com.livevote.analyticsconsumer.analyticsconsumer.dto.PollDto;
import com.livevote.analyticsconsumer.analyticsconsumer.dto.PollOptionDto;
import com.livevote.analyticsconsumer.analyticsconsumer.model.Poll;
import com.livevote.analyticsconsumer.analyticsconsumer.model.PollOption;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollOptionRepo;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollRepo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Service
public class PollCreatedConsumer implements EventConsumer<PollDto> {

    private final PollRepo pollRepository;
    private final PollOptionRepo pollOptionRepository;

    public PollCreatedConsumer(PollRepo pollRepository, PollOptionRepo pollOptionRepository, ConcurrentKafkaListenerContainerFactory<?, ?> pollCreatedKafkaListenerContainerFactory) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
    }

    @Override
    @KafkaListener(topics = "poll_created", groupId = "analytics-service-consumer", containerFactory = "pollCreatedKafkaListenerContainerFactory")
    @Transactional
    public void consume(PollDto payload) {
        try {
            processPollCreated(payload);
        } catch (Exception e) {
            System.out.println("Failed to process PollCreated event");
            throw e;
        }
    }

    public void processPollCreated(PollDto payload) {
        String pollId = payload.getPollId();

        // avoid duplicate polls
        if (pollRepository.existsById(pollId)) return;

        pollRepository.save(buildPoll(payload));

        pollOptionRepository.saveAll(buildOptions(pollId, payload));
    }

    private Poll buildPoll(PollDto payload) {
        Instant createdAt = payload.getCreatedAt();
        Instant expiresAt = createdAt.plus(Duration.ofMinutes(payload.getDurationMin()));

        Poll poll = new Poll();
        poll.setPollId(payload.getPollId());
        poll.setQuestion(payload.getQuestion());
        poll.setCreatedBy(payload.getCreatedBy());
        poll.setCreatedAt(createdAt);
        poll.setExpiresAt(expiresAt);
        poll.setStatus("OPEN");

        return poll;
    }

    private List<PollOption> buildOptions(String pollId, PollDto payload) {

        List<PollOptionDto> options = payload.getOptions();
        if (options == null) return List.of();

        return options.stream()
                .map(opt -> {
                    PollOption o = new PollOption();
                    o.setOptionId(opt.getOptionId());
                    o.setPollId(pollId);
                    o.setText(opt.getText());
                    return o;
                })
                .toList();
    }

}
