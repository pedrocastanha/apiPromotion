package org.example.domain.campaign;

import lombok.RequiredArgsConstructor;
import org.example.domain.user.User;
import org.example.domain.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

   private static final Logger logger = LoggerFactory.getLogger(CampaignController.class);

   private final UserService userService;
   private final CampaignService campaignService;
   private final MessageSource messageSource;

   @PostMapping("/generate-message")
   public ResponseEntity<CampaignRecord.CampaignMessageResponse> generateCampaignMessage(
     @RequestBody CampaignRecord.GenerateMessageRequest request,
     Authentication authentication
   ) {
      User user = (User) authentication.getPrincipal();
      logger.info(messageSource.getMessage("campaign.generate.message.request", new Object[]{user.getId()}, Locale.getDefault()));

      CampaignRecord.CampaignMessageResponse response = userService.generateCampaignMessage(user, request.prompt());

      return ResponseEntity.ok(response);
   }

   @PostMapping("/send")
   public ResponseEntity<String> sendCampaign(
     @RequestBody CampaignRecord.SendCampaignRequest request,
     Authentication authentication
   ) {
      User user = (User) authentication.getPrincipal();
      logger.info(messageSource.getMessage("campaign.schedule.message.request", new Object[]{user.getId()}, Locale.getDefault()));
      campaignService.scheduleCampaign(user, request);
      return ResponseEntity.accepted().body(messageSource.getMessage("campaign.messages.queued", null, Locale.getDefault()));
   }
}