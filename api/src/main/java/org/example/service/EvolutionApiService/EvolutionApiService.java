package org.example.service.EvolutionApiService;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.example.exception.EvolutionApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class EvolutionApiService {

   private static final Logger logger = LoggerFactory.getLogger(EvolutionApiService.class);

   private final String evolutionApiUrl;
   private final String evolutionApiKey;
   private final String evolutionInstanceName;
   private final MessageSource messageSource;

   public EvolutionApiService(
     @Value("${evolution.api.url}") String evolutionApiUrl,
     @Value("${evolution.api.key}") String evolutionApiKey,
     @Value("${evolution.api.instance}") String evolutionInstanceName,
     MessageSource messageSource
   ) {
      this.evolutionApiUrl = evolutionApiUrl;
      this.evolutionApiKey = evolutionApiKey;
      this.evolutionInstanceName = evolutionInstanceName;
      this.messageSource = messageSource;
   }

   public void sendMessage(String phoneNumber, String text) {
      String endpointUrl = String.format("%s/message/sendText/%s", evolutionApiUrl, evolutionInstanceName);

      try {
         logger.debug(messageSource.getMessage("evolution.message.sending", new Object[]{phoneNumber}, Locale.getDefault()));

         Map<String, Object> requestBody = Map.of(
           "number", phoneNumber,
           "text", text
         );

         HttpResponse<JsonNode> response = Unirest.post(endpointUrl)
           .header("apikey", evolutionApiKey)
           .header("Content-Type", "application/json")
           .body(requestBody)
           .asJson();

         if (!response.isSuccess()) {
            String responseBody = response.getBody() != null ? response.getBody().toString() : messageSource.getMessage("evolution.response.empty", null, Locale.getDefault());
            logger.error(messageSource.getMessage("evolution.message.failed", new Object[]{phoneNumber, response.getStatus(), responseBody}, Locale.getDefault()));

            throw new EvolutionApiException(
              messageSource.getMessage("evolution.api.failure", null, Locale.getDefault()),
              response.getStatus(),
              responseBody
            );
         }

         logger.info(messageSource.getMessage("evolution.message.success", new Object[]{phoneNumber, response.getStatus()}, Locale.getDefault()));

      } catch (UnirestException e) {
         logger.error(messageSource.getMessage("evolution.communication.error", new Object[]{phoneNumber, e.getMessage()}, Locale.getDefault()));
         throw new RuntimeException(messageSource.getMessage("evolution.api.communication.error", null, Locale.getDefault()), e);
      }
   }
}