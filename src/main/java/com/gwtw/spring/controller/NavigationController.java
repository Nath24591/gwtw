package com.gwtw.spring.controller;

import com.google.cloud.Timestamp;
import com.google.common.collect.Lists;
import com.gwtw.spring.CompetitionComponent;
import com.gwtw.spring.DTO.*;
import com.gwtw.spring.domain.*;
import com.gwtw.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.datastore.core.DatastoreTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.MediaType.*;

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
    @Autowired
    DatastoreTemplate datastoreTemplate;
    @Autowired
    MailController mailController;
    @Autowired
    CompetitionComponent competitionComponent;

    @RequestMapping("/")
    public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }
        //get current competitions to list as "featured" on home page
        List<Competition> competitionList = competitionComponent.getCompetitionsForHomePage();
        modelAndView.addObject("featuredCompetitions", competitionList);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping(path = "/sitemap.xml", produces = APPLICATION_XML_VALUE)
    public ModelAndView sitemap(ModelAndView modelAndView) {
        modelAndView.setViewName("sitemap.xml");
        return modelAndView;
    }

    @RequestMapping(path = "/robots.txt", produces = TEXT_PLAIN_VALUE)
    public ModelAndView robots(ModelAndView modelAndView) {
        modelAndView.setViewName("robots.txt");
        return modelAndView;
    }

    @RequestMapping("/support")
    public ModelAndView support(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }

        User user = userRepository.getUserByEmail(getEmail(request));
        if (user != null) {
            modelAndView.addObject("loggedInEmail", getEmail(request));
            modelAndView.addObject("firstName", user.getFirstName());
        }
        modelAndView.addObject("SupportDto", new SupportDto());

        modelAndView.setViewName("support");
        return modelAndView;
    }

    @RequestMapping(value = "/support", method = RequestMethod.POST)
    public ModelAndView processSupportForm(@ModelAttribute("SupportDto") SupportDto supportDto, ModelAndView modelAndView, HttpServletRequest request) {
        //Does user already have an account for this email?

        modelAndView.addObject("confirmationMessage", "Your support request has been raised, you will be contacted on the email supplied shortly. - In the meantime, please check if your question is on the FAQ page");

        mailController.createSupportEmail("gwtwcomp@gmail.com","Support request", supportDto.getName(), supportDto.getEmail(), supportDto.getContent());
        modelAndView.setViewName("support");
        return modelAndView;
    }

    @RequestMapping("/results")
    public ModelAndView results(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }
        List<CompetitionResultsDto> competitionResultsDtos = Lists.newArrayList();
        //get closed competitions
        List<Competition> closedCompetitions = competitionRepository.getCompetitionsByOpenCustom();
        for(Competition closedComp : closedCompetitions){
            CompetitionResultsDto competitionResultsDto = new CompetitionResultsDto();
            competitionResultsDto.setCompetition(closedComp);
            //get winning number
            int winningNumber = userTicketRepository.getByWinningAndCompId(1,String.valueOf(closedComp.getId())).getTicket();
            competitionResultsDto.setWinningNumber(winningNumber);
            String status = "Unclaimed";
            if(closedComp.getClaimed() == 1){
                status = "Claimed";
            }
            competitionResultsDto.setStatus(status);
            competitionResultsDtos.add(competitionResultsDto);
        }
        modelAndView.addObject("closedCompetitions", competitionResultsDtos);
        modelAndView.setViewName("results");
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
            User user = userRepository.getUserByEmail(getEmail(request));
            modelAndView.addObject("firstName", user.getFirstName());
            modelAndView.addObject("lastName", user.getLastName());
            modelAndView.addObject("email", user.getEmail());
            modelAndView.addObject("contact", user.getContactNumber());
            modelAndView.addObject("dob", user.getDateOfBirth());
            modelAndView.addObject("houseNumber", user.getHouseNumber());
            modelAndView.addObject("streetName", user.getStreetName());
            modelAndView.addObject("postcode", user.getPostcode());
            modelAndView.setViewName("myProfile");
        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("login");
        }

        return modelAndView;
    }

    @RequestMapping(value="/getentries")
    public @ResponseBody List<Entry> getEntries(HttpServletRequest request) {
        User user = userRepository.getUserByEmail(getEmail(request));
        //Entry stuff
        List<UserTicket> userTickets = userTicketRepository.getAllByUserId(String.valueOf(user.getId()));
        Map<String,ArrayList<Integer>> uniqueComps = new java.util.HashMap<>(Map.of());
        Map<String,Integer> wonComps = new java.util.HashMap<>(Map.of());
        List<Entry> entries = Lists.newArrayList();
        for(UserTicket ticket : userTickets){
            ArrayList<Integer> list;
            if (ticket.getWinning() == 1){
                wonComps.put(ticket.getCompId(),ticket.getTicket());
            }
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
                list = new ArrayList<>();
                list.add(ticket.getTicket());
                uniqueComps.put(ticket.getCompId(), list);
            }
        }


        for(Map.Entry<String,ArrayList<Integer>> uniqueComp : uniqueComps.entrySet()){
            Entry entry = new Entry();
            Long compIdInt = Long.valueOf(uniqueComp.getKey());
            Competition competition = competitionRepository.getCompetitionById(compIdInt);
            entry.setCompId(uniqueComp.getKey());
            entry.setOpen(competition.getOpen());
            entry.setCompName(competition.getHeading());
            entry.setTickets(uniqueComp.getValue());
            if(wonComps.containsKey(uniqueComp.getKey())){
                entry.setWon(1);
            } else {
                entry.setWon(0);
            }
            entry.setClaimed(competition.getClaimed());
            entries.add(entry);
        }
        return entries;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)) {
            modelAndView.addObject("loggedIn", "true");
            List<Competition> competitionList = competitionComponent.getCompetitionsForHomePage();
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
            List<Competition> competitionList = competitionComponent.getCompetitionsForHomePage();
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
        modelAndView.addObject("ResetPasswordDto", new ResetPasswordDto(token));
        if (upr == null) {
            modelAndView.addObject("errorMessage", "Invalid password reset token");
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

    @RequestMapping(value = "/claim", method = RequestMethod.GET)
    public ModelAndView claim(ModelAndView modelAndView, HttpServletRequest request,@RequestParam("id") String compId) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
            modelAndView.addObject("loggedInEmail", getEmail(request));
            Long compIdInt = Long.valueOf(compId);
            Competition competition = competitionRepository.getCompetitionById(compIdInt);
            if(competition.getClaimed() == 1){
                modelAndView.addObject("errorMessage", "You have already claimed for this competition.");
            }
            //Did this user win this competition?
            User user = userRepository.getUserByEmail(getEmail(request));
            UserTicket userTicket = userTicketRepository.getByUserIdAndCompIdAndWinning(String.valueOf(user.getId()), compId, 1);
            modelAndView.addObject("comp", competition);
            if (userTicket == null) {
                modelAndView.addObject("errorMessage", "You have not won this competition!");
            }
            modelAndView.setViewName("claim");
        } else {
            modelAndView.addObject("LoginDto", new LoginDto());
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/claim", method = RequestMethod.POST)
    public ModelAndView processResetForm(@ModelAttribute("ClaimDto") ClaimDto claimDto, ModelAndView  modelAndView, HttpServletRequest request){
        //Does user already have an account for this email?
        if(isUserLoggedIn(request)) {
            modelAndView.addObject("loggedIn", "true");
            modelAndView.addObject("loggedInEmail", getEmail(request));
        }
        User user = userRepository.getUserByEmail(claimDto.getEmail());
        UserTicket userTicket = userTicketRepository.getByUserIdAndCompIdAndWinning(String.valueOf(user.getId()), claimDto.getCompId(), 1);
        if (userTicket == null) {
            modelAndView.addObject("errorMessage", "This is not your competition win to claim.");
        } else {
            Long compIdInt = Long.valueOf(claimDto.getCompId());
            Competition competition = competitionRepository.getCompetitionById(compIdInt);
            competition.setClaimed(1);
            String prizeOrCash = claimDto.getPrizeorcash();
            ClaimedPrize claimedPrize = new ClaimedPrize();
            if (prizeOrCash.equals("cash")) {
                claimedPrize.setClaimedCash(1);
                claimedPrize.setClaimedPrize(0);
            } else {
                claimedPrize.setClaimedCash(0);
                claimedPrize.setClaimedPrize(1);
            }
            claimedPrize.setCompId(claimDto.getCompId());
            claimedPrize.setUserId(String.valueOf(user.getId()));
            competition.setClaimed(1);
            datastoreTemplate.save(claimedPrize);
            datastoreTemplate.save(competition);
            modelAndView.addObject("confirmationMessage", "Claim successful!");
            modelAndView.addObject("comp", competition);
        }

        modelAndView.setViewName("claim");
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

    @RequestMapping(value = "/current-competitions", method = RequestMethod.GET)
    public ModelAndView currentCompetitions(ModelAndView modelAndView, HttpServletRequest request) {
        if(isUserLoggedIn(request)){
            modelAndView.addObject("loggedIn", "true");
        }

        //get current competitons from database and add as array object
        List<Competition> competitionList = competitionRepository.getCompetitionByRemainingIsGreaterThan(0);
        modelAndView.addObject("competitions", competitionList);

        modelAndView.setViewName("current-competitions");
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
