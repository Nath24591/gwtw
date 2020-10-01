package com.gwtw.spring.controller;

import com.gwtw.spring.CompetitionComponent;
import com.gwtw.spring.DTO.ForgotPasswordDto;
import com.gwtw.spring.DTO.LoginDto;
import com.gwtw.spring.DTO.ResetPasswordDto;
import com.gwtw.spring.DTO.UserDto;
import com.gwtw.spring.PasswordUtils;
import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.domain.User;
import com.gwtw.spring.domain.UserPasswordReset;
import com.gwtw.spring.repository.CompetitionRepository;
import com.gwtw.spring.repository.UserPasswordResetRepository;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    DatastoreTemplate datastoreTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    UserPasswordResetRepository userPasswordResetRepository;
    @Autowired
    MailController mailController;
    @Autowired
    CompetitionComponent competitionComponent;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(@ModelAttribute("UserDto")UserDto userDto, ModelAndView  modelAndView, HttpServletRequest request){
        //Does user already have an account for this email?
        User existingUsers = userRepository.getUserByEmail(userDto.getEmail().toLowerCase());
        if(existingUsers != null) {
            modelAndView.addObject("errorMessage", "An account is already registered with this email address!");
            modelAndView.setViewName("signup");
            return modelAndView;
        }
        HttpSession session = request.getSession();
        session.setAttribute( "email" , userDto.getEmail());

        // Generate Salt. The generated value can be stored in DB.
        String salt = PasswordUtils.getSalt(30);
        String encryptedPassword = PasswordUtils.generateSecurePassword(userDto.getPassword(), salt);

        List<Competition> competitionList = competitionComponent.getCompetitionsForHomePage();
        modelAndView.addObject("featuredCompetitions", competitionList);
        modelAndView.addObject("loggedIn", "true");
        modelAndView.setViewName("index");
        User u = new User(null,userDto.getFirstName(), userDto.getLastName(), userDto.getEmail().toLowerCase(), userDto.getContactNumber(), userDto.getDateOfBirth(), userDto.getHouseNumber(), userDto.getStreetName(), userDto.getPostcode(), encryptedPassword, salt);
        this.datastoreTemplate.save(u);
        mailController.createRegistrationEmail(u.getEmail(),"Thank you for registering", u.getFirstName());
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView processLoginForm(@ModelAttribute("LoginDto") LoginDto loginDto, ModelAndView  modelAndView, HttpServletRequest request) {
        User existingUser = userRepository.getUserByEmail(loginDto.getEmail().toLowerCase());
        HttpSession session = request.getSession();
        if(existingUser != null){
            boolean passwordsMatch = PasswordUtils.verifyUserPassword(loginDto.getPassword(), existingUser.getPassword(), existingUser.getSalt());
            //check password matches
            if(passwordsMatch){
                session.setAttribute("email", existingUser.getEmail());
                modelAndView.addObject("loggedIn", "true");
                List<Competition> competitionList =competitionComponent.getCompetitionsForHomePage();
                modelAndView.addObject("featuredCompetitions", competitionList);
                modelAndView.setViewName("index");
            } else {
                modelAndView.addObject("errorMessage", "Wrong email or password");
                modelAndView.setViewName("login");
            }
        } else {
            modelAndView.addObject("errorMessage", "Wrong email or password");
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ModelAndView processForgotPasswordForm(@ModelAttribute("ForgotPasswordDto") ForgotPasswordDto forgotPasswordDto, ModelAndView  modelAndView, HttpServletRequest request){
        //Does user already have an account for this email?
        User existingUser = userRepository.getUserByEmail(forgotPasswordDto.getEmail());
        if(existingUser != null) {
            String token = generateRandomString();
            UserPasswordReset upr = new UserPasswordReset();
            upr.setEmail(existingUser.getEmail());
            upr.setToken(token);
            this.datastoreTemplate.save(upr);
            mailController.createPasswordResetEmail(existingUser.getEmail(),"Password reset request", existingUser.getFirstName(), token);
            modelAndView.addObject("confirmationMessage", "A password reset link has been emailed to you");
        } else {
            modelAndView.addObject("errorMessage", "There isn't an account under that email on our system");
        }
        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView processResetForm(@ModelAttribute("ResetPasswordDto") ResetPasswordDto resetPasswordDto, ModelAndView  modelAndView, HttpServletRequest request){
        //Does user already have an account for this email?
        UserPasswordReset upr = userPasswordResetRepository.getUserPasswordResetByToken(resetPasswordDto.getToken());
        User existingUser = userRepository.getUserByEmail(upr.getEmail());
        if(existingUser != null) {
            // Generate Salt. The generated value can be stored in DB.
            String salt = PasswordUtils.getSalt(30);
            String encryptedPassword = PasswordUtils.generateSecurePassword(resetPasswordDto.getPassword(), salt);
            existingUser.setPassword(encryptedPassword);
            existingUser.setSalt(salt);
            this.datastoreTemplate.save(existingUser);
            this.datastoreTemplate.delete(upr);
            modelAndView.addObject("confirmationMessage", "You have successfully set your password, please log in using your new details.");
        }
        modelAndView.setViewName("resetPassword");
        return modelAndView;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView processLogout(ModelAndView  modelAndView, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("email");
        List<Competition> competitionList = competitionComponent.getCompetitionsForHomePage();
        modelAndView.addObject("featuredCompetitions", competitionList);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    public String generateRandomString() {
        return UUID.randomUUID().toString();
    }

}
