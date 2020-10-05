package com.gwtw.spring.DTO;

import com.sun.istack.NotNull;

public class GiveTicketDto {
    @NotNull
    private String email;

    private String competitionId;

    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
