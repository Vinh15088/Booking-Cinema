package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.service.MailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "MAIL_CONTROLLER")
@Tag(name = "Mail Controller")
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @GetMapping("send-mail")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public void sendMail(@RequestParam String to,
                         @RequestParam String subject,
                         @RequestParam String body) throws MessagingException {
        log.info("Send mail to {}", to);

        mailService.sendMail(to, subject, body);

        log.info("Send mail sucessfully");
    }

    @GetMapping("send-verify-mail")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public void sendVerifyMail(@RequestParam String to,
                               @RequestParam String username
                               ) throws MessagingException {
        log.info("Send mail to {}", to);

        mailService.verifyMail(to, username);

        log.info("Send mail sucessfully");
    }
}
