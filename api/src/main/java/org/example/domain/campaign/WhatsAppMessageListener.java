package org.example.domain.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.config.RedisQueueConfig;
import org.example.service.EvolutionApiService.EvolutionApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class WhatsAppMessageListener {
   private static final Logger logger = LoggerFactory.getLogger(WhatsAppMessageListener.class);

   private final StringRedisTemplate redisTemplate;
   private final EvolutionApiService evolutionApiService;
   private final ObjectMapper objectMapper;
   private final MessageSource messageSource;

   @Scheduled(fixedDelayString = "PT1S")
   public void processWhatsAppQueue() {
      logger.trace(messageSource.getMessage("whatsapp.queue.check", null, Locale.getDefault()));
      try {
         String messageJson = redisTemplate.opsForList().leftPop(RedisQueueConfig.WHATSAPP_MESSAGES_QUEUE, 5, TimeUnit.SECONDS);

         if (messageJson != null) {
            logger.info(messageSource.getMessage("whatsapp.message.consumed", null, Locale.getDefault()));
            CampaignRecord.WhatsAppMessage message = objectMapper.readValue(messageJson, CampaignRecord.WhatsAppMessage.class);

            evolutionApiService.sendMessage(message.phoneNumber(), message.text());
            logger.debug(messageSource.getMessage("whatsapp.message.processed", new Object[]{message.phoneNumber()}, Locale.getDefault()));
         }
      } catch (Exception e) {
         logger.error(messageSource.getMessage("whatsapp.message.error", new Object[]{e.getMessage()}, Locale.getDefault()));
      }
   }
}
