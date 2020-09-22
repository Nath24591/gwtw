package com.gwtw.spring;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

public interface EmailService {
        void sendMessageUsingFreemarkerTemplate(String to,
                                                String subject,
                                                Map<String, Object> templateModel, String templateName)
                throws IOException, TemplateException, MessagingException;
}
