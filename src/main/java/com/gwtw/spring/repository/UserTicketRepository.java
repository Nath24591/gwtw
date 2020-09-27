package com.gwtw.spring.repository;

import com.gwtw.spring.domain.UserTicket;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTicketRepository  extends DatastoreRepository<UserTicket, Long> {
    List<UserTicket> getAllByUserId(String userId);

    UserTicket getByUserIdAndCompIdAndWinning(String userId, String compId, Integer winning);

    UserTicket getByWinningAndCompId(Integer winning, String compId);

    List<UserTicket> getAllByCompId(String compId);
}

