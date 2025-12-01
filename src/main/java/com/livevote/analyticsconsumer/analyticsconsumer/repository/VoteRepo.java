package com.livevote.analyticsconsumer.analyticsconsumer.repository;

import com.livevote.analyticsconsumer.analyticsconsumer.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepo extends JpaRepository<Vote, String> {

    List<Vote> findByPollId(String pollId);

    List<Vote> findByPollIdAndOptionId(String pollId, String optionId);
}
