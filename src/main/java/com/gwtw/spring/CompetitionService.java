package com.gwtw.spring;

import com.google.cloud.Timestamp;
import com.gwtw.spring.controller.MailController;
import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.domain.User;
import com.gwtw.spring.domain.UserTicket;
import com.gwtw.spring.repository.CompetitionRepository;
import com.gwtw.spring.repository.UserRepository;
import com.gwtw.spring.repository.UserTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@EnableScheduling
public class CompetitionService {

    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    UserTicketRepository userTicketRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MailController mailController;
    @Autowired
    DatastoreTemplate datastoreTemplate;


    //Scan for competitions to close every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    @PostConstruct
    private void closeSoldOutCompetitions() {
        Competition competition = competitionRepository.getCompetitionByRemainingIs(0);

        if(competition != null && competition.getOpen() == 1){
            List<UserTicket> userTickets = userTicketRepository.getAllByCompId(String.valueOf(competition.getId()));

            // get winner
            Random rand = new Random();
            UserTicket winningTicket = userTickets.get(rand.nextInt(userTickets.size()));
            winningTicket.setWinning(1);
            datastoreTemplate.save(winningTicket);

            // close competition
            competition.setOpen(0);
            competition.setDrawnDate(Timestamp.now());
            datastoreTemplate.save(competition);

            //close all user tickets for that competition
            Map<String, User> uniqueEmails = new java.util.HashMap<>(Map.of());
            for (UserTicket userTicket : userTickets) {
                User user = userRepository.getUserById(Long.valueOf(userTicket.getUserId()));
                uniqueEmails.put(user.getEmail(), user);
                userTicket.setOpen(0);
            }

            // for all unique users, generate an email.
            for(Map.Entry<String, User> user: uniqueEmails.entrySet()){
                mailController.createCompetitionClosedEmail(user.getKey(),"Competition has been drawn!", user.getValue().getFirstName(), competition.getHeading());
            }

            // save ticket changes
            datastoreTemplate.saveAll(userTickets);
        }
    }
}
