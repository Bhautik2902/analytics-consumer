package com.livevote.analyticsconsumer.analyticsconsumer.repository;

import com.livevote.analyticsconsumer.analyticsconsumer.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepo extends JpaRepository<Poll, String> {
}
