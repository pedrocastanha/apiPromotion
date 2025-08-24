package org.example.domain.campaign;

import lombok.RequiredArgsConstructor;
import org.example.domain.user.User;
import org.example.domain.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns" )
@RequiredArgsConstructor
public class CampaignController {

   private static final Logger logger = LoggerFactory.getLogger(CampaignController.class);

   private final UserService userService;
   private final CampaignService campaignService;

   @PostMapping("/generate-message")
   public ResponseEntity<CampaignRecord.CampaignMessageResponse> generateCampaignMessage(
     @RequestBody CampaignRecord.GenerateMessageRequest request,
     Authentication authentication
   ) {
      User user = (User) authentication.getPrincipal();
      logger.info("Requisição para gerar mensagem para o usuário ID: {}", user.getId());
      String generatedMessage = userService.generateCampaignMessage(user, request.prompt());
      return ResponseEntity.ok(new CampaignRecord.CampaignMessageResponse(generatedMessage));
   }

   @PostMapping("/send")
   public ResponseEntity<String> sendCampaign(
     @RequestBody CampaignRecord.SendCampaignRequest request,
     Authentication authentication
   ) {
      User user = (User) authentication.getPrincipal();
      logger.info("Requisição para agendar mensagem para o usuário ID: {}", user.getId());
      campaignService.scheduleCampaign(user, request);
      return ResponseEntity.accepted().body("As mensagens estão sendo enfileiradas para envio.");
   }
}
