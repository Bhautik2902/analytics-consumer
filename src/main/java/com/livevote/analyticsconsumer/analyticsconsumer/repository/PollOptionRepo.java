package com.livevote.analyticsconsumer.analyticsconsumer.repository;

import com.livevote.analyticsconsumer.analyticsconsumer.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollOptionRepo extends JpaRepository<PollOption, String> {

    List<PollOption> findByPollId(String pollId);

}
