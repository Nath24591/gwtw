package com.gwtw.spring.domain;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity(name = "claimed_prizes")
public class ClaimedPrize {
    @Id
    private Long id;

    private String userId;

    private String compId;

    private Integer claimedPrize;

    private Integer claimedCash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }

    public Integer getClaimedPrize() {
        return claimedPrize;
    }

    public void setClaimedPrize(Integer claimedPrize) {
        this.claimedPrize = claimedPrize;
    }

    public Integer getClaimedCash() {
        return claimedCash;
    }

    public void setClaimedCash(Integer claimedCash) {
        this.claimedCash = claimedCash;
    }
}
