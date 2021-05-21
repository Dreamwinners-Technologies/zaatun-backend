package com.zaatun.zaatunecommerce.utils;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@AllArgsConstructor
public class EmailUtil {

    private final JavaMailSender javaMailSender;

    public boolean sendMail(@RequestParam String email,
                                @RequestParam String text,
                                @RequestParam String subject)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom("zaatundevteam@gmail.com", "Zaatun Dev Team");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(text, true);

        try {
            javaMailSender.send(msg);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }

    }
}
