package com.livevote.analyticsconsumer.analyticsconsumer.controller;

import com.livevote.analyticsconsumer.analyticsconsumer.model.Poll;
import com.livevote.analyticsconsumer.analyticsconsumer.model.PollOption;
import com.livevote.analyticsconsumer.analyticsconsumer.model.Vote;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollOptionRepo;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.PollRepo;
import com.livevote.analyticsconsumer.analyticsconsumer.repository.VoteRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final PollRepo pollRepository;
    private final PollOptionRepo pollOptionRepository;
    private final VoteRepo voteRepository;

    public AnalyticsController(PollRepo pollRepository,
                               PollOptionRepo pollOptionRepository,
                               VoteRepo voteRepository) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.voteRepository = voteRepository;
    }

    // POLL APIS
    @GetMapping("/polls")
    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }

    @GetMapping("/polls/{pollId}")
    public Optional<Poll> getPollById(@PathVariable String pollId) {
        return pollRepository.findById(pollId);
    }

    // POLL OPTION APIS
    @GetMapping("/polls/{pollId}/options")
    public List<PollOption> getOptionsByPoll(@PathVariable String pollId) {
        return pollOptionRepository.findByPollId(pollId);
    }

    // VOTE APIS
    @GetMapping("/polls/{pollId}/votes")
    public List<Vote> getVotesByPoll(@PathVariable String pollId) {
        return voteRepository.findByPollId(pollId);
    }

    @GetMapping("/polls/{pollId}/options/{optionId}/votes")
    public List<Vote> getVotesByOption(@PathVariable String pollId, @PathVariable String optionId) {
        return voteRepository.findByPollIdAndOptionId(pollId, optionId);
    }
}

