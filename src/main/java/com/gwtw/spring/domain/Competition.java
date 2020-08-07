package com.gwtw.spring.domain;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity(name = "competitions")
public class Competition {

    @Id
    private Long id;

    private String image;

    private String heading;

    private String description;

    private String price;

    private int remaining;

    private int startingTickets;

    public Competition() {

    }

    public Competition(Long id,String image, String heading, String description, String price, int startingTickets, int remaining) {
        this.id = id;
        this.heading = heading;
        this.description = description;
        this.image = image;
        this.price = price;
        this.startingTickets = startingTickets;
        this.remaining = remaining;
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
}
