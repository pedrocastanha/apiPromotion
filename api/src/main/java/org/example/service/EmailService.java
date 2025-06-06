package org.example.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    // Remetente configurável via application.properties
    @Value("${spring.mail.username}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia um e-mail simples (texto puro).
     * @param para destinatário
     * @param assunto assunto do e-mail
     * @param corpo corpo do e-mail em texto simples
     */
    public void enviarEmailSimples(String para, String assunto, String corpo) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, false, "UTF-8");

            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(corpo, false); // false = texto simples
            helper.setFrom(remetente);

            mailSender.send(mensagem);

            logger.info("Email simples enviado para {}", para);

        } catch (MailException | MessagingException e) {
            logger.error("Erro ao enviar email simples para {}: {}", para, e.getMessage());
        }
    }

    /**
     * Envia um e-mail com corpo HTML.
     * @param para destinatário
     * @param assunto assunto do e-mail
     * @param corpoHtml corpo do e-mail em HTML
     */
    public void enviarEmailHtml(String para, String assunto, String corpoHtml) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true); // true = corpo em HTML
            helper.setFrom(remetente);

            mailSender.send(mensagem);

            logger.info("Email HTML enviado para {}", para);

        } catch (MailException | MessagingException e) {
            logger.error("Erro ao enviar email HTML para {}: {}", para, e.getMessage());
            // Trate exceções conforme sua regra de negócio
        }
    }
}

