package org.example.domain.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.config.RedisQueueConfig;
import org.example.domain.client.Client;
import org.example.domain.client.ClientRepository;
import org.example.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
   private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);

   private final ClientRepository clientRepository;
   private final StringRedisTemplate redisTemplate;
   private final ObjectMapper objectMapper;

   private String extractFirstName(String fullName) {
      if (fullName == null || fullName.trim().isEmpty()) {
         return "Cliente";
      }
      String[] nameParts = fullName.trim().split("\\s+");
      return nameParts[0];
   }

   @Override
   public void scheduleCampaign(User user, CampaignRecord.SendCampaignRequest request) {
      logger.info("Iniciando agendamento para o usuário ID: {}. Tipo de alvo: {}", user.getId(), request.targetingType());

      List<Client> targetClients = findTargetClients(Long.valueOf(user.getId()), request);
      logger.info("Campanha para o usuário ID: {}. Total de {} clientes encontrados para enfileirar.", user.getId(), targetClients.size());

      for (Client client : targetClients) {
         try {
            String personalizedMessage = request.approvedMessage().replace("[nome do cliente]", extractFirstName(client.getName()));
            CampaignRecord.WhatsAppMessage message = new CampaignRecord.WhatsAppMessage(client.getPhoneNumber(), personalizedMessage);
            String messageJson = objectMapper.writeValueAsString(message);

            redisTemplate.opsForList().rightPush(RedisQueueConfig.WHATSAPP_MESSAGES_QUEUE, messageJson);
            logger.debug("Mensagem para o cliente ID: {} enfileirada com sucesso.", client.getId());

         } catch (Exception e) {
            logger.error("Falha ao serializar ou enfileirar mensagem para o cliente ID: {}. Erro: {}", client.getId(), e.getMessage());
         }
      }
      logger.info("Todas as mensagens para a campanha do usuário ID: {} foram enfileiradas.", user.getId());
   }

   private List<Client> findTargetClients(Long userId, CampaignRecord.SendCampaignRequest request) {
      logger.debug("Buscando clientes para o usuário ID: {} com a estratégia: {}", userId, request.targetingType());
      return switch (request.targetingType()) {
         case ALL -> clientRepository.findAllByUserId(userId);

         case SPECIFIC -> clientRepository.findAllById(request.specificClientIds());

         case LAST_PURCHASE -> clientRepository.findAllByUserIdAndLastPurchaseDateBetween(
           userId,
           request.lastPurchaseRange().startDate(),
           request.lastPurchaseRange().endDate()
         );
      };
   }
}
