package com.gwtw.spring.domain;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity(name = "competition_tickets")
public class CompetitionTicket {
    @Id
    private Long id;

    private String competitionId;

    private Integer ticket;

    private Timestamp reservedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

    public Integer getTicket() {
        return ticket;
    }

    public void setTicket(Integer ticket) {
        this.ticket = ticket;
    }

    public Timestamp getReservedTime() {
        return reservedTime;
    }

    public void setReservedTime(Timestamp reservedTime) {
        this.reservedTime = reservedTime;
    }
}
