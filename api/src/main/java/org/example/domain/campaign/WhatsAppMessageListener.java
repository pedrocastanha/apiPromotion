package org.example.domain.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.config.RedisQueueConfig;
import org.example.service.EvolutionApiService.EvolutionApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class WhatsAppMessageListener {
   private static final Logger logger = LoggerFactory.getLogger(WhatsAppMessageListener.class);

   private final StringRedisTemplate redisTemplate;
   private final EvolutionApiService evolutionApiService;
   private final ObjectMapper objectMapper;

   @Scheduled(fixedDelayString = "PT1S")
   public void processWhatsAppQueue() {
      logger.trace("Verificando a fila do WhatsApp...");
      try {
         String messageJson = redisTemplate.opsForList().leftPop(RedisQueueConfig.WHATSAPP_MESSAGES_QUEUE, 5, TimeUnit.SECONDS);

         if (messageJson != null) {
            logger.info("Nova mensagem consumida da fila.");
            CampaignRecord.WhatsAppMessage message = objectMapper.readValue(messageJson, CampaignRecord.WhatsAppMessage.class);

            evolutionApiService.sendMessage(message.phoneNumber(), message.text());
            logger.debug("Mensagem para o número {} processada e enviada com sucesso.", message.phoneNumber());
         }
      } catch (Exception e) {
         logger.error("Erro ao processar mensagem da fila do WhatsApp. Mensagem pode ter sido perdida. Erro: {}", e.getMessage());
      }
   }
}
