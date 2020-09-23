package com.gwtw.spring.controller;

import com.google.cloud.Timestamp;
import com.google.common.collect.Lists;
import com.gwtw.spring.DTO.ForgotPasswordDto;
import com.gwtw.spring.DTO.LoginDto;
import com.gwtw.spring.DTO.ResetPasswordDto;
import com.gwtw.spring.DTO.UserDto;
import com.gwtw.spring.domain.*;
import com.gwtw.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class NavigationController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    CompetitionTicketRepository competitionTicketRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserTicketRepository userTicketRepository;
    @Autowired
    UserPasswordResetRepository userPasswordResetRepository;

    @RequestMapping("/")
    public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }
        //get current competitions to list as "featured" on home page
        List<Competition> competitionList = competitionRepository.getCompetitionsForHomePage(0);
        modelAndView.addObject("featuredCompetitions", competitionList);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping("/faq")
    public ModelAndView faq(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }
        modelAndView.setViewName("faq");
        return modelAndView;
    }

    @RequestMapping("/myProfile")
    public ModelAndView myProfile(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
            //Profile stuff
            User user = userRepository.getUsersByEmail(getEmail(request)).get(0);
            modelAndView.addObject("firstName", user.getFirstName());
            modelAndView.addObject("lastName", user.getLastName());
            modelAndView.addObject("email", user.getEmail());
            modelAndView.addObject("contact", user.getContactNumber());
            modelAndView.addObject("dob", user.getDateOfBirth());
            modelAndView.addObject("houseNumber", user.getHouseNumber());
            modelAndView.addObject("streetName", user.getStreetName());
            modelAndView.addObject("postcode", user.getPostcode());
            //Entry stuff
            List<UserTicket> userTickets = userTicketRepository.getAllByUserId(String.valueOf(user.getId()));
            Map<String,ArrayList<Integer>> uniqueComps = new java.util.HashMap<>(Map.of());
            List<Entry> entries = Lists.newArrayList();
            for(UserTicket ticket : userTickets){
                ArrayList<Integer> list;
                if(uniqueComps.containsKey(ticket.getCompId())){
                    // if the key has already been used,
                    // we'll just grab the array list and add the value to it
                    list = uniqueComps.get(ticket.getCompId());
                    list.add(ticket.getTicket());
                    list.sort(Comparator.naturalOrder());
                } else {
                    // if the key hasn't been used yet,
                    // we'll create a new ArrayList<String> object, add the value
                    // and put it in the array list with the new key
                    list = new ArrayList<Integer>();
                    list.add(ticket.getTicket());
                    uniqueComps.put(ticket.getCompId(), list);
                }
            }

            for(Map.Entry<String,ArrayList<Integer>> uniqueComp : uniqueComps.entrySet()){
                Entry entry = new Entry();
                Long compIdInt = Long.valueOf(uniqueComp.getKey());
                Competition competition = competitionRepository.getCompetitionById(compIdInt);
                entry.setCompId(uniqueComp.getKey());
                entry.setCompName(competition.getHeading());
                entry.setTickets(uniqueComp.getValue());
                entries.add(entry);
            }
            modelAndView.addObject("userTickets", entries);
        }
        modelAndView.setViewName("myProfile");
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)) {
            modelAndView.addObject("loggedIn", "true");
            List<Competition> competitionList = competitionRepository.getCompetitionsForHomePage(0);
            modelAndView.addObject("featuredCompetitions", competitionList);
            modelAndView.setViewName("index");
        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView forgotPassword(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)) {
            modelAndView.addObject("loggedIn", "true");
            List<Competition> competitionList = competitionRepository.getCompetitionsForHomePage(0);
            modelAndView.addObject("featuredCompetitions", competitionList);
            modelAndView.setViewName("index");
        } else {
            modelAndView.addObject("ForgotPasswordDto", new ForgotPasswordDto());
            modelAndView.setViewName("forgotPassword");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public ModelAndView resetPassword(ModelAndView modelAndView, HttpServletRequest request, @RequestParam("token") String token) {

        UserPasswordReset upr = userPasswordResetRepository.getUserPasswordResetByToken(token);
        if(upr != null){
            modelAndView.addObject("ResetPasswordDto", new ResetPasswordDto(token));
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.addObject("ResetPasswordDto", new ResetPasswordDto(token));
            modelAndView.addObject("errorMessage", "Invalid password reset token");
            modelAndView.setViewName("resetPassword");
        }

        modelAndView.addObject("ResetPasswordDto", new ResetPasswordDto(token));
        modelAndView.setViewName("resetPassword");

        return modelAndView;
    }

    @RequestMapping(value = "/comp", method = RequestMethod.GET)
    public ModelAndView singleComp(ModelAndView modelAndView, HttpServletRequest request,@RequestParam("id") String compId) {
        //get competition using compId
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
            modelAndView.addObject("loggedInEmail", getEmail(request));
        }
        Long compIdInt = Long.valueOf(compId);
        Competition competition = competitionRepository.getCompetitionById(compIdInt);
        Question question = questionRepository.getQuestion();
        modelAndView.addObject("comp", competition);
        modelAndView.addObject("question", question);
        //Get available tickets
        LocalDateTime currentTime = LocalDateTime.now().minusMinutes(15);
        Timestamp timestamp = Timestamp.of(java.sql.Timestamp.valueOf(currentTime));
        List<CompetitionTicket> availableTickets = competitionTicketRepository.getAllByCompetitionIdAndReservedTimeIsLessThan(compId, timestamp);
        availableTickets.sort(Comparator.comparingInt(CompetitionTicket::getTicket));
        modelAndView.addObject("availableTickets", availableTickets);

        modelAndView.setViewName("comp");
        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView register(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }
        modelAndView.addObject("UserDto", new UserDto());
        modelAndView.setViewName("signup");
        return modelAndView;
    }

    @RequestMapping(value = "/currentcomps", method = RequestMethod.GET)
    public ModelAndView currentComps(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }

        //get current competitons from database and add as array object
        List<Competition> competitionList = competitionRepository.getCompetitionByRemainingIsGreaterThan(0);
        modelAndView.addObject("competitions", competitionList);

        modelAndView.setViewName("currentcomps");
        return modelAndView;
    }

    private String getEmail(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return String.valueOf(session.getAttribute("email"));
    }

    private boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getAttribute("email") != null;
    }
}
