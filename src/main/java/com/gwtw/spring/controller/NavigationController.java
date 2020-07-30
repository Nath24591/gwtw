package com.gwtw.spring.controller;

import com.gwtw.spring.domain.User;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    
}
