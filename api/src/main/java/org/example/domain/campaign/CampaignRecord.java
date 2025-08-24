package org.example.domain.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class CampaignRecord {

   public record GenerateMessageRequest(
     @NotBlank(message = "{error.validation.notBlank") String prompt
   ){ }

   public record CampaignMessageResponse(String generatedMessage) { }

   public enum TargetingType { ALL, SPECIFIC, LAST_PURCHASE }
   public record SendCampaignRequest(
     @NotBlank(message = "{error.validation.notBlank}") String approvedMessage,
     @NotNull(message = "{error.validation.notNull}") TargetingType targetingType,
     List<Integer> specificClientIds,
     DateRange lastPurchaseRange
   ) {
      public SendCampaignRequest {
         if (targetingType == TargetingType.SPECIFIC && (specificClientIds == null || specificClientIds.isEmpty())) {
            throw new IllegalArgumentException("{error.campaign.specificClientsRequired}");
         }
         if (targetingType == TargetingType.LAST_PURCHASE && lastPurchaseRange == null) {
            throw new IllegalArgumentException("{error.campaign.dateRangeRequired}");
         }
      }
   }

   public record DateRange(
     @NotNull(message = "{error.validation.notNull}") LocalDate startDate,
     @NotNull(message = "{error.validation.notNull}") LocalDate endDate
   ) {}

   public record WhatsAppMessage(
     String phoneNumber,
     String text
   ) {}
}
