package org.example.service.EvolutionApiService;

import jakarta.validation.constraints.NotBlank;

public class EvolutionApiRecord {
   public record SendMessageOptions(
     Long delay,
     String presence
   ){}

   public record SendMessageRequest(
     @NotBlank String number,
     @NotBlank String text,
     SendMessageOptions sendMessageOptions
   ){}
}
