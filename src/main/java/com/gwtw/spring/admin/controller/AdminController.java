package com.gwtw.spring.admin.controller;

import com.google.cloud.Timestamp;
import com.google.common.collect.Lists;
import com.gwtw.spring.DTO.CompetitionDto;
import com.gwtw.spring.DTO.GiveTicketDto;
import com.gwtw.spring.DTO.LoginDto;
import com.gwtw.spring.DTO.QuestionDto;
import com.gwtw.spring.PasswordUtils;
import com.gwtw.spring.controller.MailController;
import com.gwtw.spring.domain.*;
import com.gwtw.spring.repository.CompetitionRepository;
import com.gwtw.spring.repository.CompetitionTicketRepository;
import com.gwtw.spring.repository.UserRepository;
import com.gwtw.spring.repository.UserTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class AdminController {
    @Autowired
    DatastoreTemplate datastoreTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    CompetitionTicketRepository competitionTicketRepository;
    @Autowired
    MailController mailController;
    @Autowired
    UserTicketRepository userTicketRepository;

    @RequestMapping("/admin")
    public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
        //if an admin, set view to admin else set to admin login
        if(isUserAdmin(request)){
            List<Competition> competitions = Lists.newArrayList();
            competitions.addAll(competitionRepository.getCompetitionByRemainingIsGreaterThan(0));
            modelAndView.addObject("competitions", competitions);
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
                List<Competition> competitions = Lists.newArrayList();
                competitions.addAll(competitionRepository.getCompetitionByRemainingIsGreaterThan(0));
                modelAndView.addObject("competitions", competitions);
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

    @RequestMapping("/giveTicket")
    public ModelAndView giveTicket(ModelAndView modelAndView, HttpServletRequest request) {
        //if an admin, set view to giveTickets else set to admin login
        if(isUserAdmin(request)){
            modelAndView.addObject("GiveTicketDto", new GiveTicketDto());
            List<User> users = Lists.newArrayList();
            for(User user : userRepository.findAll()){
                users.add(user);
            }

            List<Competition> competitions = Lists.newArrayList();
            competitions.addAll(competitionRepository.getCompetitionByRemainingIsGreaterThan(0));

            modelAndView.addObject("competitions", competitions);
            modelAndView.addObject("users", users);
            modelAndView.setViewName("giveTicket");

        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("adminLogin");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/giveTicket", method = RequestMethod.POST)
    public ModelAndView processTicketForm(@ModelAttribute("GiveTicketDto") GiveTicketDto giveTicketDto, ModelAndView modelAndView, HttpServletRequest request) {
        //if an admin, set view to giveTickets else set to admin login
        if(isUserAdmin(request)){
            //get user to give ticket
            List<User> users = Lists.newArrayList();
            for(User user : userRepository.findAll()){
                users.add(user);
            }
            User user = userRepository.getUserByEmail(giveTicketDto.getEmail());

            //get competition to get ticket from
            Competition competition = competitionRepository.getCompetitionById(Long.valueOf(giveTicketDto.getCompetitionId()));

            //get available tickets for competition
            LocalDateTime currentTime = LocalDateTime.now().minusMinutes(15);
            Timestamp timestamp = Timestamp.of(java.sql.Timestamp.valueOf(currentTime));
            List<CompetitionTicket> availableTickets = competitionTicketRepository.getAllByCompetitionIdAndReservedTimeIsLessThan(giveTicketDto.getCompetitionId(), timestamp);

            //Remove 1 from competition
            competition.setRemaining(competition.getRemaining()-1);
            datastoreTemplate.save(competition);

            //Select random ticket
            Random rand = new Random();
            CompetitionTicket ticket = availableTickets.get(rand.nextInt(availableTickets.size()));

            //create the user ticket
            UserTicket userTicket = new UserTicket();
            userTicket.setCompId(giveTicketDto.getCompetitionId());
            userTicket.setUserId(String.valueOf(user.getId()));
            userTicket.setTicket(ticket.getTicket());
            userTicket.setOpen(1);
            userTicket.setWinning(0);
            userTicket.setFree(1);
            userTicket.setPurchasedTime(Timestamp.now());
            //Delete ticket from competition tickets
            CompetitionTicket ticketToDelete = competitionTicketRepository.getCompetitionTicketByCompetitionIdAndTicket(giveTicketDto.getCompetitionId(),ticket.getTicket());
            datastoreTemplate.delete(ticketToDelete);

            //add ticket to user
            datastoreTemplate.save(userTicket);

            //Email free entry confirmation
            mailController.createFreeEntryConfirmationEmail(user.getEmail(), "You have been given a free entry", user.getFirstName(), competition.getHeading(), userTicket.getTicket().toString());

            List<Competition> competitions = Lists.newArrayList();
            competitions.addAll(competitionRepository.getCompetitionByRemainingIsGreaterThan(0));

            modelAndView.addObject("confirmationMessage", "You have given " + user.getFirstName() + " 1 free entry to " + competition.getHeading() + ", their ticket number is: " + ticket.getTicket());
            modelAndView.addObject("competitions", competitions);
            modelAndView.addObject("users", users);
            modelAndView.setViewName("giveTicket");

        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("adminLogin");
        }
        return modelAndView;
    }

    @RequestMapping(value="/getallentries")
    public @ResponseBody
    List<EntryCount> getAllEntries(@RequestParam("id") String compId) {
            //Entry stuff
            Competition competition = competitionRepository.getCompetitionById(Long.valueOf(compId));
            List<UserTicket> userTickets = userTicketRepository.getAllByCompId(compId);
            Map<String, ArrayList<Integer>> uniqueComps = new java.util.HashMap<>(Map.of());
            List<EntryCount> entries = Lists.newArrayList();
            for (UserTicket ticket : userTickets) {
                ArrayList<Integer> list;
                if (uniqueComps.containsKey(ticket.getUserId())) {
                    // if the key has already been used,
                    // we'll just grab the array list and add the value to it
                    list = uniqueComps.get(ticket.getUserId());
                    list.add(ticket.getTicket());
                    list.sort(Comparator.naturalOrder());
                } else {
                    // if the key hasn't been used yet,
                    // we'll create a new ArrayList<String> object, add the value
                    // and put it in the array list with the new key
                    list = new ArrayList<>();
                    list.add(ticket.getTicket());
                    uniqueComps.put(ticket.getUserId(), list);
                }
            }

            for (Map.Entry<String, ArrayList<Integer>> uniqueComp : uniqueComps.entrySet()) {
                EntryCount entryCount = new EntryCount();
                User user = userRepository.getUserById(Long.valueOf(uniqueComp.getKey()));
                entryCount.setEmail(user.getEmail());
                entryCount.setTickets(uniqueComp.getValue());
                entryCount.setCount(uniqueComp.getValue().size());
                entries.add(entryCount);
            }
        return entries;
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
