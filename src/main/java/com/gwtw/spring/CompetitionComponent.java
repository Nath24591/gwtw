package com.gwtw.spring;

import com.gwtw.spring.domain.Competition;
import com.gwtw.spring.repository.CompetitionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class CompetitionComponent {
    private final CompetitionRepository competitionRepository;
    private volatile List<Competition> competitions = null;

    CompetitionComponent(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public List<Competition> getCompetitionsForHomePage() {
        return competitions;
    }

    //Every 5mins
    @Scheduled(cron = "0 */5 * * * *")
    @PostConstruct
    private void populateCompetitionCache() {
        this.competitions = competitionRepository.getCompetitionsForHomePage(0);
    }
}

