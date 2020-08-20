package com.gwtw.spring.domain;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.util.HashMap;
import java.util.Map;

@Entity(name = "questions")
public class Question {

    @Id
    private Long id;

    private String question;

    private Map<String, Boolean> answers = new HashMap<String, Boolean>();



    public Question() {

    }

    public Question(Long id, String question, Map answers) {
        this.id = id;
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Boolean> answers) {
        this.answers = answers;
    }
}
