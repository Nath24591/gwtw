package com.gwtw.spring.domain;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity(name = "user_tickets")
public class UserTicket {
    @Id
    private Long id;

    private String userId;

    private String compId;

    private Integer ticket;

    private int open;

    private int winning;

    private Timestamp purchasedTime;

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }

    public Integer getTicket() {
        return ticket;
    }

    public void setTicket(Integer ticket) {
        this.ticket = ticket;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getWinning() {
        return winning;
    }

    public void setWinning(int winning) {
        this.winning = winning;
    }

    public Timestamp getPurchasedTime() {
        return purchasedTime;
    }

    public void setPurchasedTime(Timestamp purchasedTime) {
        this.purchasedTime = purchasedTime;
    }
}
