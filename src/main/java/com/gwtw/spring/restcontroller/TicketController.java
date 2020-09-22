package com.gwtw.spring.restcontroller;

import com.google.cloud.Timestamp;
import com.google.common.collect.Lists;
import com.gwtw.spring.DTO.PurchaseTicketsDto;
import com.gwtw.spring.DTO.TicketUpdateDto;
import com.gwtw.spring.controller.MailController;
import com.gwtw.spring.domain.*;
import com.gwtw.spring.repository.CompetitionRepository;
import com.gwtw.spring.repository.CompetitionTicketRepository;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    CompetitionTicketRepository competitionTicketRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DatastoreTemplate datastoreTemplate;
    @Autowired
    MailController mailController;

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

    @PutMapping(value = "/purchaseTickets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> purchaseTickets(@PathVariable(value = "id") String id, @RequestBody PurchaseTicketsDto purchaseTicketsDto) {

        String response = "";
        List<String> tickets = purchaseTicketsDto.getTickets();
        String email = purchaseTicketsDto.getEmail();
        User user = userRepository.getUsersByEmail(email).get(0);
        List<UserTicket> userTickets = Lists.newArrayList();
        Competition currentComp = competitionRepository.getCompetitionById(Long.valueOf(id));
        currentComp.setRemaining(currentComp.getRemaining()-tickets.size());
        datastoreTemplate.save(currentComp);

        for (String ticketNum : tickets) {
            UserTicket userTicket = new UserTicket();
            userTicket.setCompId(id);
            userTicket.setUserId(String.valueOf(user.getId()));
            userTicket.setTicket(Integer.parseInt(ticketNum));
            userTicket.setOpen(1);
            userTicket.setWinning(0);
            userTicket.setPurchasedTime(Timestamp.now());
            userTickets.add(userTicket);
            CompetitionTicket ticketToDelete = competitionTicketRepository.getCompetitionTicketByCompetitionIdAndTicket(id,Integer.parseInt(ticketNum));
            datastoreTemplate.delete(ticketToDelete);
        }

        DecimalFormat df = new DecimalFormat("0.00");
        double doubleCost = tickets.size() * Double.parseDouble(currentComp.getPrice());
        String moneyString = df.format(doubleCost);

        datastoreTemplate.saveAll(userTickets);
        mailController.createPurchaseConfirmationEmail(user.getEmail(), "Thank you for order", user.getFirstName(), currentComp.getHeading(), tickets.toString(), moneyString);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
