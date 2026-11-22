package com.lecfantasy.service;

import com.lecfantasy.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Envía correos si hay SMTP configurado (variables SPRING_MAIL_HOST, SPRING_MAIL_USERNAME...).
 * Si no lo hay, escribe el correo en el log para poder probar en local.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final ObjectProvider<JavaMailSender> sender;
    private final AppProperties props;

    public MailService(ObjectProvider<JavaMailSender> sender, AppProperties props) {
        this.sender = sender;
        this.props = props;
    }

    @Async
    public void enviar(String para, String asunto, String cuerpo) {
        JavaMailSender mailSender = sender.getIfAvailable();
        if (mailSender == null) {
            log.info("[CORREO SIN SMTP] Para: {} | Asunto: {}\n{}", para, asunto, cuerpo);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(props.mail().from());
            msg.setTo(para);
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo a {}: {}", para, e.getMessage());
        }
    }
}
