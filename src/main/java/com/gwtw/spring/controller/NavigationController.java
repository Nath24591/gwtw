package com.gwtw.spring.controller;

import com.gwtw.spring.DTO.UserDto;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NavigationController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/index.html")
    public String indexHTML() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView register(ModelAndView modelAndView) {
        modelAndView.addObject("UserDto", new UserDto());
        modelAndView.setViewName("signup");
        return modelAndView;
    }
}
