package com.gwtw.spring.domain;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Unindexed;
import org.springframework.data.annotation.Id;

@Entity(name = "competitions")
public class Competition {

    @Id
    private Long id;

    @Unindexed
    private String image;

    private String heading;

    private String description;

    private String price;

    private String cost;

    private int remaining;

    private int startingTickets;

    private int open;

    private int claimed;

    private Timestamp drawnDate;

    public Competition() {

    }

    public Competition(Long id,String image, String heading, String description, String price, int startingTickets, int remaining, String cost) {
        this.id = id;
        this.heading = heading;
        this.description = description;
        this.image = image;
        this.price = price;
        this.startingTickets = startingTickets;
        this.remaining = remaining;
        this.open = 1;
        this.claimed = 0;
        this.drawnDate = Timestamp.now();
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getStartingTickets() {
        return startingTickets;
    }

    public void setStartingTickets(int startingTickets) {
        this.startingTickets = startingTickets;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClaimed() {
        return claimed;
    }

    public void setClaimed(int claimed) {
        this.claimed = claimed;
    }

    public Timestamp getDrawnDate() {
        return drawnDate;
    }

    public void setDrawnDate(Timestamp drawnDate) {
        this.drawnDate = drawnDate;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
