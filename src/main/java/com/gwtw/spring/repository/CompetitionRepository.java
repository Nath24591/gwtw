package com.gwtw.spring.repository;

import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.domain.User;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionRepository extends DatastoreRepository<Competition, Long> {
    List<Competition> getCompetitionByRemainingIsGreaterThan(int remaining);
}
