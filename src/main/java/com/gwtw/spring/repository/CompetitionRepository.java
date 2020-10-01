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

    @Query("SELECT * FROM competitions WHERE remaining > @remaining_val order by remaining asc limit 5")
    List<Competition> getCompetitionsForHomePage(@Param("remaining_val")int remaining);

    @Query("SELECT * FROM competitions WHERE open = 0 order by drawnDate desc")
    List<Competition> getCompetitionsByOpenCustom();

    Competition getCompetitionByRemainingIs(int remaining);

    Competition getCompetitionById(Long id);
}
