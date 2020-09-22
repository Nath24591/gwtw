package com.gwtw.spring;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

@Service("emailService")
public class EmailServiceImpl implements EmailService
{
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    @Override
    public void sendMessageUsingFreemarkerTemplate(
            String to, String subject, Map<String, Object> templateModel, String templateName)
            throws IOException, TemplateException, MessagingException {

        Template freemarkerTemplate = freemarkerConfig.createConfiguration().getTemplate(templateName);
        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);

        sendHtmlMessage(to, subject, htmlBody);
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("admin@gonewiththewin.co.uk");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}
