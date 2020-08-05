package com.gwtw.spring.admin.controller;

import com.gwtw.spring.DTO.LoginDto;
import com.gwtw.spring.PasswordUtils;
import com.gwtw.spring.domain.User;
import com.gwtw.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

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
        List<User> existingUsers = userRepository.getUsersByEmail(loginDto.getEmail());
        HttpSession session = request.getSession();
        if(existingUsers.size() > 0){
            User user = existingUsers.get(0);

            boolean passwordsMatch = PasswordUtils.verifyUserPassword(loginDto.getPassword(), user.getPassword(), user.getSalt());
            //check password matches
            if(passwordsMatch && user.getIsAdmin() == 1){
                session.setAttribute("email", user.getEmail());
                session.setAttribute("admin", "true");
                modelAndView.setViewName("admin");
                return modelAndView;
            } else {
                modelAndView.addObject("errorMessage", "Wrong email or password or you're not a admin");
                modelAndView.setViewName("adminLogin");
                return modelAndView;
            }
        } else {
            modelAndView.addObject("errorMessage", "Wrong email or password or you're not a admin");
            modelAndView.setViewName("adminLogin");
            return modelAndView;
        }
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
