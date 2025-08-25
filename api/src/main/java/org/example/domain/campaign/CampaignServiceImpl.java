package org.example.domain.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.config.RedisQueueConfig;
import org.example.domain.client.Client;
import org.example.domain.client.ClientRepository;
import org.example.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
   private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);

   private final ClientRepository clientRepository;
   private final StringRedisTemplate redisTemplate;
   private final ObjectMapper objectMapper;
   private final MessageSource messageSource;

   private String extractFirstName(String fullName) {
      if (fullName == null || fullName.trim().isEmpty()) {
         return "Cliente";
      }
      String[] nameParts = fullName.trim().split("\\s+");
      return nameParts[0];
   }

   @Override
   public void scheduleCampaign(User user, CampaignRecord.SendCampaignRequest request) {
      logger.info(messageSource.getMessage("campaign.schedule.start", new Object[]{user.getId(), request.targetingType()}, Locale.getDefault()));

      List<Client> targetClients = findTargetClients(Long.valueOf(user.getId()), request);
      logger.info(messageSource.getMessage("campaign.clients.found", new Object[]{user.getId(), targetClients.size()}, Locale.getDefault()));

      if (targetClients.isEmpty()) {
         logger.warn(messageSource.getMessage("campaign.clients.empty", new Object[]{user.getId()}, Locale.getDefault()));
         return;
      }

      for (Client client : targetClients) {
         try {
            String personalizedMessage = request.approvedMessage().replace("[nome do cliente]", extractFirstName(client.getName()));
            CampaignRecord.WhatsAppMessage message = new CampaignRecord.WhatsAppMessage(client.getPhoneNumber(), personalizedMessage);
            String messageJson = objectMapper.writeValueAsString(message);

            redisTemplate.opsForList().rightPush(RedisQueueConfig.WHATSAPP_MESSAGES_QUEUE, messageJson);
            logger.debug(messageSource.getMessage("campaign.message.queued", new Object[]{client.getId()}, Locale.getDefault()));

         } catch (Exception e) {
            logger.error(messageSource.getMessage("campaign.message.error", new Object[]{client.getId(), e.getMessage()}, Locale.getDefault()));
         }
      }
      logger.info(messageSource.getMessage("campaign.schedule.complete", new Object[]{user.getId()}, Locale.getDefault()));
   }

   private List<Client> findTargetClients(Long userId, CampaignRecord.SendCampaignRequest request) {
      logger.debug(messageSource.getMessage("campaign.clients.search", new Object[]{userId, request.targetingType()}, Locale.getDefault()));
      return switch (request.targetingType()) {
         case ALL -> clientRepository.findAllByUserId(userId);

         case SPECIFIC -> clientRepository.findAllById(request.specificClientIds());

         case LAST_PURCHASE -> clientRepository.findAllByUserIdAndLastPurchaseBetween(
           userId,
           request.lastPurchaseRange().startDate(),
           request.lastPurchaseRange().endDate()
         );
      };
   }
}