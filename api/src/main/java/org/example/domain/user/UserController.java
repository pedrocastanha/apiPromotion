package org.example.domain.user;

import lombok.RequiredArgsConstructor;
import org.example.domain.client.ClientRecord;
import org.example.domain.client.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
   private final ClientService clientService;
   private final UserService userService;

   @GetMapping("/profile")
   public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
      User user = (User) authentication.getPrincipal();
      return ResponseEntity.ok(UserResponse.fromUser(user));
   }

   @ResponseStatus(value = HttpStatus.OK)
   @GetMapping("/list")
   public ResponseEntity<List<ClientRecord.clientListDTO>> listByUserId(@RequestParam("userId") Long userId) {
      return ResponseEntity.ok(clientService.getClientsByUserId(userId));
   }

   @ResponseStatus(value = HttpStatus.OK)
   @PostMapping("/message-bot")
   public ResponseEntity<String>sendMessage(@RequestBody UserRecord.ChatMessageRequest request) {
      return ResponseEntity.ok(userService.sendChatMessage(request.userId(), request.message()));
   }
}
