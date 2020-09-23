package com.gwtw.spring.domain;

import java.util.List;

public class Entry {
    private String compName;
    private String compId;
    private List<Integer> tickets;
    private int open;

    public List<Integer> getTickets() {
        return tickets;
    }

    public void setTickets(List<Integer> tickets) {
        this.tickets = tickets;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }
}
