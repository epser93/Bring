package com.web.blog.Member.service;

import com.web.blog.Member.model.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${aws.ses.port}")
    private String smtpPort;

    @Value("${aws.ses.host}")
    private String sesHost;

    @Value("${aws.ses.username}")
    private String sesUsername;

    @Value("${aws.ses.password}")
    private String sesPassword;

    public void send(Email email) throws MessagingException, UnsupportedEncodingException {
        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);

        System.out.println(email.getSender());

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getSender(), "SSAFY.BLOG"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getReceiver()));
        msg.setSubject(email.getTitle());
        msg.setContent(email.getContent(), "text/html");

        Transport transport = session.getTransport();

        try {
            transport.connect(sesHost, sesUsername, sesPassword);
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
        } finally {
            transport.close();
        }
    }

}
