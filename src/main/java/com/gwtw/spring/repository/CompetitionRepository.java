package com.gwtw.spring.repository;

import com.gwtw.spring.domain.Competition;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionRepository extends DatastoreRepository<Competition, Long> {
    List<Competition> getCompetitionByRemainingIsGreaterThan(int remaining);

    @Query("SELECT * FROM competitions WHERE remaining > @remaining_val order by remaining desc limit 5")
    List<Competition> getCompetitionsForHomePage(@Param("remaining_val")int remaining);
}
