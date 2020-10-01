package com.gwtw.spring.admin.controller;

import com.google.cloud.Timestamp;
import com.google.common.collect.Lists;
import com.gwtw.spring.DTO.CompetitionDto;
import com.gwtw.spring.DTO.LoginDto;
import com.gwtw.spring.DTO.QuestionDto;
import com.gwtw.spring.PasswordUtils;
import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.domain.CompetitionTicket;
import com.gwtw.spring.domain.Question;
import com.gwtw.spring.domain.User;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    DatastoreTemplate datastoreTemplate;
    @Autowired
    UserRepository userRepository;

    @RequestMapping("/admin")
    public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
        //if an admin, set view to admin else set to admin login
        if(isUserAdmin(request)){
            modelAndView.setViewName("admin");
        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("adminLogin");
        }
        return modelAndView;
    }


    @RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
    public ModelAndView processAdminLoginForm(@ModelAttribute("LoginDto") LoginDto loginDto, ModelAndView  modelAndView, HttpServletRequest request) {
        User existingUser = userRepository.getUserByEmail(loginDto.getEmail());
        HttpSession session = request.getSession();
        if(existingUser != null){
            boolean passwordsMatch = PasswordUtils.verifyUserPassword(loginDto.getPassword(), existingUser.getPassword(), existingUser.getSalt());
            //check password matches
            if(passwordsMatch && existingUser.getIsAdmin() == 1){
                session.setAttribute("email", existingUser.getEmail());
                session.setAttribute("admin", "true");
                modelAndView.setViewName("admin");
            } else {
                modelAndView.addObject("errorMessage", "Wrong email or password or you're not a admin");
                modelAndView.setViewName("adminLogin");
            }
        } else {
            modelAndView.addObject("errorMessage", "Wrong email or password or you're not a admin");
            modelAndView.setViewName("adminLogin");
        }
        return modelAndView;
    }

    @RequestMapping("/addCompetitions")
    public ModelAndView addCompetitions(ModelAndView modelAndView, HttpServletRequest request) {
        //if an admin, set view to addCompetitions else set to admin login
        if(isUserAdmin(request)){
            modelAndView.addObject("CompetitionDto", new CompetitionDto());
            modelAndView.setViewName("addCompetitions");
        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("adminLogin");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/addCompetitions", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(@ModelAttribute("CompetitionDto") CompetitionDto competitionDto, ModelAndView  modelAndView, HttpServletRequest request) {
        //Does user already have an account for this email?

        modelAndView.addObject("CompetitionDto", new CompetitionDto());
        modelAndView.addObject("confirmationMessage", "Competition Added!");
        modelAndView.setViewName("addCompetitions");
        Competition c = new Competition(null, competitionDto.getImage(), competitionDto.getHeading(), competitionDto.getDescription(), competitionDto.getPrice(), competitionDto.getStartingTickets(), competitionDto.getStartingTickets(), competitionDto.getCost());
        this.datastoreTemplate.save(c);

        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        Timestamp timestamp = Timestamp.of(java.sql.Timestamp.valueOf(fifteenMinutesAgo));
        String compId = String.valueOf(c.getId());
        List<CompetitionTicket> competitionTickets = Lists.newArrayList();
        int numberOfTickets = competitionDto.getStartingTickets();
        for (int i = 1; i < numberOfTickets+1; i++) {
            CompetitionTicket competitionTicket = new CompetitionTicket();
            competitionTicket.setCompetitionId(compId);
            competitionTicket.setTicket(i);
            competitionTicket.setReservedTime(timestamp);
            competitionTickets.add(competitionTicket);
        }

        datastoreTemplate.saveAll(competitionTickets);
        return modelAndView;
    }

    @RequestMapping("/addQuestion")
    public ModelAndView addQuestions(ModelAndView modelAndView, HttpServletRequest request) {
        //if an admin, set view to addCompetitions else set to admin login
        if(isUserAdmin(request)){
            modelAndView.addObject("QuestionDto", new QuestionDto());
            modelAndView.setViewName("addQuestion");
        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("adminLogin");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/addQuestion", method = RequestMethod.POST)
    public ModelAndView addQuestions(@ModelAttribute("QuestionDto") QuestionDto questionDto, ModelAndView  modelAndView) {
        //Does user already have an account for this email?
        modelAndView.addObject("QuestionDto", new QuestionDto());
        modelAndView.addObject("confirmationMessage", "Question Added!");
        modelAndView.setViewName("addQuestion");
        Map<String, Boolean> answers = new HashMap<>();
        answers.put(questionDto.getWrongAnswer1(), false);
        answers.put(questionDto.getWrongAnswer2(), false);
        answers.put(questionDto.getWrongAnswer3(), false);
        answers.put(questionDto.getCorrectAnswer(), true);
        Question q = new Question(null, questionDto.getQuestion(), answers );
        this.datastoreTemplate.save(q);
        return modelAndView;
    }

    @RequestMapping(value = "/adminLogout", method = RequestMethod.GET)
    public ModelAndView processLogout(ModelAndView  modelAndView, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("email");
        session.removeAttribute("admin");
        modelAndView.addObject("LoginDto", new LoginDto());
        modelAndView.setViewName("adminLogin");
        return modelAndView;
    }

    private boolean isUserAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getAttribute("admin") != null;
    }
}
