package com.gwtw.spring.controller;

import com.gwtw.spring.EmailServiceImpl;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailController {

    @Autowired
    public EmailServiceImpl emailSenderService;

    public void createPurchaseConfirmationEmail(String to, String subject, String recipientName, String compHeader, String tickets, String cost) {

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientName", recipientName);
        templateModel.put("compHeader", compHeader);
        templateModel.put("tickets", tickets);
        templateModel.put("cost", cost);

        try {
            emailSenderService.sendMessageUsingFreemarkerTemplate(
                    to,
                    subject,
                    templateModel,"purchase-confirmation.ftl");
        } catch (IOException | TemplateException | MessagingException e) {
            e.printStackTrace();
        }
    }

    public void createRegistrationEmail(String to, String subject, String recipientName) {

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientName", recipientName);

        try {
            emailSenderService.sendMessageUsingFreemarkerTemplate(
                    to,
                    subject,
                    templateModel, "registration.ftl");
        } catch (IOException | TemplateException | MessagingException e) {
            e.printStackTrace();
        }
    }

    public void createCompetitionClosedEmail(String to, String subject, String recipientName, String compHeading) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientName", recipientName);
        templateModel.put("compHeader", compHeading);

        try {
            emailSenderService.sendMessageUsingFreemarkerTemplate(
                    to,
                    subject,
                    templateModel, "competition-drawn.ftl");
        } catch (IOException | TemplateException | MessagingException e) {
            e.printStackTrace();
        }
    }

    public void createPasswordResetEmail(String to, String subject, String recipientName, String token) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientName", recipientName);
        templateModel.put("token", token);

        try {
            emailSenderService.sendMessageUsingFreemarkerTemplate(
                    to,
                    subject,
                    templateModel, "forgot-password.ftl");
        } catch (IOException | TemplateException | MessagingException e) {
            e.printStackTrace();
        }
    }
}
