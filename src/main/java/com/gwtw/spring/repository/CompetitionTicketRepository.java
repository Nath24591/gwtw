package com.gwtw.spring.repository;

import com.google.cloud.Timestamp;
import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.domain.CompetitionTicket;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.cloud.gcp.data.datastore.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionTicketRepository  extends DatastoreRepository<CompetitionTicket, Long> {

    CompetitionTicket getCompetitionTicketByCompetitionIdAndTicket(String compId,Integer ticket);

    List<CompetitionTicket> getAllByCompetitionIdAndReservedTimeIsLessThan(String compId, Timestamp timestamp);
}
