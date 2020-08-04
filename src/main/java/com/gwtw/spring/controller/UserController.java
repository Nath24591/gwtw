package com.gwtw.spring.controller;

import com.gwtw.spring.DTO.UserDto;
import com.gwtw.spring.domain.User;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    DatastoreTemplate datastoreTemplate;
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(@ModelAttribute("UserDto")UserDto userDto, ModelAndView  modelAndView ) {
        //Does user already have an account for this email?
        List<User> existingUsers = userRepository.getUsersByEmail(userDto.getEmail());
        if(existingUsers.size() > 0){
            modelAndView.addObject("errorMessage", "An account is already registered with this email address!");
            modelAndView.setViewName("signup");
            return modelAndView;
        }
        modelAndView.addObject("UserDto", new UserDto());
        modelAndView.addObject("confirmationMessage", "fanx");
        modelAndView.setViewName("signup");
        User u = new User(null,userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(), userDto.getPassword());
        this.datastoreTemplate.save(u);
        return modelAndView;
    }
}
