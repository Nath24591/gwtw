package com.gwtw.spring.DTO;

import com.gwtw.spring.domain.Competition;

public class CompetitionResultsDto {
    private Competition competition;

    private Integer winningNumber;

    private String status;

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Integer getWinningNumber() {
        return winningNumber;
    }

    public void setWinningNumber(Integer winningNumber) {
        this.winningNumber = winningNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
