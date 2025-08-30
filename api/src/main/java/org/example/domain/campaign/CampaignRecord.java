package org.example.domain.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class CampaignRecord {
   private static final MessageSource messageSource = null;

   public record GenerateMessageRequest(
     @NotBlank(message = "{campaign.validation.notBlank}") String prompt
   ){}

   public record CampaignMessageResponse(String generatedMessage) { }

   public enum TargetingType { ALL, SPECIFIC, LAST_PURCHASE }
   public record SendCampaignRequest(
     @NotBlank(message = "{campaign.validation.notBlank}") String approvedMessage,
     @NotNull(message = "{campaign.validation.notNull}") TargetingType targetingType,
     List<Integer> specificClientIds,
     DateRange lastPurchaseRange
   ) {
      public SendCampaignRequest {
         if (targetingType == TargetingType.SPECIFIC && (specificClientIds == null || specificClientIds.isEmpty())) {
            throw new IllegalArgumentException(messageSource.getMessage("campaign.specificClientsRequired", null, Locale.getDefault()));
         }
         if (targetingType == TargetingType.LAST_PURCHASE && lastPurchaseRange == null) {
            throw new IllegalArgumentException(messageSource.getMessage("campaign.dateRangeRequired", null, Locale.getDefault()));
         }
      }
   }

   public record DateRange(
     @NotNull(message = "{campaign.validation.notNull}") LocalDate startDate,
     @NotNull(message = "{campaign.validation.notNull}") LocalDate endDate
   ) {}

   public record WhatsAppMessage(
     String phoneNumber,
     String text
   ) {}
}