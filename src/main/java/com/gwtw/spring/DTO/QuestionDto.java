package com.gwtw.spring.DTO;

import com.sun.istack.NotNull;

public class QuestionDto {
    @NotNull
    private  String question;
    @NotNull
    private  String wrongAnswer1;
    @NotNull
    private  String wrongAnswer2;
    @NotNull
    private  String wrongAnswer3;
    @NotNull
    private  String correctAnswer;


    public  String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public  String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {

        this.wrongAnswer1 = wrongAnswer1;
    }

    public  String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public  String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }

    public  String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
