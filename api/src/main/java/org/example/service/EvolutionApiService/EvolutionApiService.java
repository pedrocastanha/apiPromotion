package org.example.service.EvolutionApiService;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvolutionApiService {

   private static final Logger logger = LoggerFactory.getLogger(EvolutionApiService.class);

   private final String evolutionApiUrl;
   private final String evolutionApiKey;
   private final String evolutionInstanceName;

   public EvolutionApiService(
     @Value("${evolution.api.url}") String evolutionApiUrl,
     @Value("${evolution.api.key}") String evolutionApiKey,
     @Value("${evolution.api.instance}") String evolutionInstanceName
   ) {
      this.evolutionApiUrl = evolutionApiUrl;
      this.evolutionApiKey = evolutionApiKey;
      this.evolutionInstanceName = evolutionInstanceName;
   }

   public void sendMessage(String phoneNumber, String text) {
      String endpointUrl = String.format("%s/message/sendText/%s", evolutionApiUrl, evolutionInstanceName);

      try {
         logger.debug("Enviando mensagem para {} via Evolution API.", phoneNumber);

         Map<String, Object> requestBody = Map.of(
           "number", phoneNumber,
           "text", text
         );

         HttpResponse<JsonNode> response = Unirest.post(endpointUrl)
           .header("apikey", evolutionApiKey)
           .header("Content-Type", "application/json")
           .body(requestBody)
           .asJson();

         if (response.isSuccess()) {
            logger.info("Mensagem para {} enviada com sucesso. Status: {}", phoneNumber, response.getStatus());
         } else {
            String responseBody = response.getBody() != null ? response.getBody().toString() : "Corpo da resposta vazio";
            logger.error("Falha ao enviar mensagem para {}. Status: {}. Resposta: {}",
              phoneNumber, response.getStatus(), responseBody);
            throw new RuntimeException("Falha na API da Evolution: " + response.getStatusText());
         }

      } catch (Exception e) {
         logger.error("Erro de comunicação ao tentar enviar mensagem para {}. Erro: {}", phoneNumber, e.getMessage());
         throw new RuntimeException("Erro de comunicação com a Evolution API.", e);
      }
   }
}
