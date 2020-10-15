package com.gwtw.spring.domain;

import java.util.List;

public class EntryCount {
    private String email;
    private List<Integer> tickets;
    private Integer count;

    public String getEmail() {
        return email;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getTickets() {
        return tickets;
    }

    public void setTickets(List<Integer> tickets) {
        this.tickets = tickets;
    }


}
