package com.gwtw.spring.controller;

import com.google.cloud.Timestamp;
import com.gwtw.spring.DTO.LoginDto;
import com.gwtw.spring.DTO.UserDto;
import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.domain.CompetitionTicket;
import com.gwtw.spring.domain.Question;
import com.gwtw.spring.domain.User;
import com.gwtw.spring.repository.CompetitionRepository;
import com.gwtw.spring.repository.CompetitionTicketRepository;
import com.gwtw.spring.repository.QuestionRepository;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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
            User user = userRepository.getUsersByEmail(getEmail(request)).get(0);
            modelAndView.addObject("firstName", user.getFirstName());
            modelAndView.addObject("lastName", user.getLastName());
            modelAndView.addObject("email", user.getEmail());
            modelAndView.addObject("contact", user.getContactNumber());
            modelAndView.addObject("dob", user.getDateOfBirth());
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
