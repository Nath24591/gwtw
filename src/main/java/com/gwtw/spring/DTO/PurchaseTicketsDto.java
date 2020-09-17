package com.gwtw.spring.DTO;

import java.util.List;

public class PurchaseTicketsDto {
    private List<String> tickets;
    private String email;

    public List<String> getTickets() {
        return tickets;
    }

    public void setTicketNumber(List<String> tickets) {
        this.tickets = tickets;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
