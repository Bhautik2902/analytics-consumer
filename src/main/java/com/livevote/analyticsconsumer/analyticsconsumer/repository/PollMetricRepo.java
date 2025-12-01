package com.livevote.analyticsconsumer.analyticsconsumer.repository;

import com.livevote.analyticsconsumer.analyticsconsumer.model.PollMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PollMetricRepo extends JpaRepository<PollMetric, String> {

    Optional<PollMetric> findByPollIdAndOptionId(String pollId, String optionId);

}
