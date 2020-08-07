package com.gwtw.spring.restcontroller;

import com.google.cloud.Timestamp;
import com.gwtw.spring.DTO.TicketUpdateDto;
import com.gwtw.spring.domain.CompetitionTicket;
import com.gwtw.spring.repository.CompetitionTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    CompetitionTicketRepository competitionTicketRepository;
    @Autowired
    DatastoreTemplate datastoreTemplate;

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> reserveTicket(@PathVariable(value = "id") String id, @RequestBody TicketUpdateDto ticketUpdateDto){

        String response = "";
        Integer ticketInt = Integer.parseInt(ticketUpdateDto.getTicketNumber());

        CompetitionTicket ticketToUpdate = competitionTicketRepository.getCompetitionTicketByCompetitionIdAndTicket(id,ticketInt);
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        Timestamp timestamp = Timestamp.of(java.sql.Timestamp.valueOf(fifteenMinutesAgo));
        if(ticketToUpdate.getReservedTime().compareTo(timestamp) > 0){
            response = "reserved";
            //ticket is reserved
            System.out.println("this condition");
        } else {
            response = "success";
            ticketToUpdate.setReservedTime(Timestamp.now());
            this.datastoreTemplate.save(ticketToUpdate);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/unreserve/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> unreserveTicket(@PathVariable(value = "id") String id, @RequestBody TicketUpdateDto ticketUpdateDto){

        String response = "success";
        Integer ticketInt = Integer.parseInt(ticketUpdateDto.getTicketNumber());
        CompetitionTicket ticketToUpdate = competitionTicketRepository.getCompetitionTicketByCompetitionIdAndTicket(id,ticketInt);
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        Timestamp timestamp = Timestamp.of(java.sql.Timestamp.valueOf(fifteenMinutesAgo));
        ticketToUpdate.setReservedTime(timestamp);
        this.datastoreTemplate.save(ticketToUpdate);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
