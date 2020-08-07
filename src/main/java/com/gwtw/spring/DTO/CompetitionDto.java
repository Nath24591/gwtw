package com.gwtw.spring.DTO;

import com.sun.istack.NotNull;

public class CompetitionDto {
    @NotNull
    private String heading;
    @NotNull
    private String description;
    @NotNull
    private String price;
    @NotNull
    private int startingTickets;
    @NotNull
    private String image;

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

    public int getStartingTickets() {
        return startingTickets;
    }

    public void setStartingTickets(int startingTickets) {
        this.startingTickets = startingTickets;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
