package com.gwtw.spring.controller;

import com.google.common.collect.Lists;
import com.gwtw.spring.DTO.LoginDto;
import com.gwtw.spring.DTO.UserDto;
import com.gwtw.spring.PasswordUtils;
import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.domain.CompetitionTicket;
import com.gwtw.spring.domain.User;
import com.gwtw.spring.repository.CompetitionRepository;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    DatastoreTemplate datastoreTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompetitionRepository competitionRepository;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(@ModelAttribute("UserDto")UserDto userDto, ModelAndView  modelAndView, HttpServletRequest request) {
        //Does user already have an account for this email?
        List<User> existingUsers = userRepository.getUsersByEmail(userDto.getEmail());
        if(existingUsers.size() > 0) {
            modelAndView.addObject("errorMessage", "An account is already registered with this email address!");
            modelAndView.setViewName("signup");
            return modelAndView;
        }
        HttpSession session = request.getSession();
        session.setAttribute( "email" , userDto.getEmail());

        // Generate Salt. The generated value can be stored in DB.
        String salt = PasswordUtils.getSalt(30);
        String encryptedPassword = PasswordUtils.generateSecurePassword(userDto.getPassword(), salt);

        List<Competition> competitionList = competitionRepository.getCompetitionsForHomePage(0);
        modelAndView.addObject("featuredCompetitions", competitionList);
        modelAndView.addObject("loggedIn", "true");
        modelAndView.setViewName("index");
        User u = new User(null,userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(), userDto.getContactNumber(), userDto.getDateOfBirth(), userDto.getHouseNumber(), userDto.getStreetName(), userDto.getPostcode(), encryptedPassword, salt);
        this.datastoreTemplate.save(u);
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView processLoginForm(@ModelAttribute("LoginDto") LoginDto loginDto, ModelAndView  modelAndView, HttpServletRequest request) {
        List<User> existingUsers = userRepository.getUsersByEmail(loginDto.getEmail());
        HttpSession session = request.getSession();
        if(existingUsers.size() > 0){
            User user = existingUsers.get(0);
            boolean passwordsMatch = PasswordUtils.verifyUserPassword(loginDto.getPassword(), user.getPassword(), user.getSalt());
            //check password matches
            if(passwordsMatch){
                session.setAttribute("email", user.getEmail());
                modelAndView.addObject("loggedIn", "true");
                List<Competition> competitionList = competitionRepository.getCompetitionsForHomePage(0);
                modelAndView.addObject("featuredCompetitions", competitionList);
                modelAndView.setViewName("index");
                return modelAndView;
            } else {
                modelAndView.addObject("errorMessage", "Wrong email or password");
                modelAndView.setViewName("login");
                return modelAndView;
            }
        } else {
            modelAndView.addObject("errorMessage", "Wrong email or password");
            modelAndView.setViewName("login");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView processLogout(ModelAndView  modelAndView, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("email");
        modelAndView.setViewName("index");
        return modelAndView;
    }

}
