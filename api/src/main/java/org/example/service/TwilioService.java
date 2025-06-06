package org.example.service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {
    @Value("${twilio.account_sid}")
    private String accountSid;
    @Value("${twilio.auth_token}")
    private String authToken;
    @Value("${twilio.from_number}")
    private String fromNumber; // número autorizado do WhatsApp

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void enviarWhatsApp(String toNumber, String message) {
        try {
            Message.creator(
                    new com.twilio.type.PhoneNumber("whatsapp:" + toNumber),
                    new com.twilio.type.PhoneNumber("whatsapp:" + fromNumber),
                    message
            ).create();
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem Twilio: " + e.getMessage());
            throw e;
        }
    }
}

