package org.example.domain.user;

import lombok.RequiredArgsConstructor;
import org.example.domain.client.ClientRecord;
import org.example.domain.client.ClientService;
import org.example.exception.CustomExceptions;
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

   @GetMapping("/profile")
   public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
      if (authentication == null || authentication.getPrincipal() == null) {
         throw new CustomExceptions.AuthenticationException("Usuário não autenticado");
      }
      User user = (User) authentication.getPrincipal();
      try {
         return ResponseEntity.ok(UserResponse.fromUser(user));
      } catch (Exception e) {
         throw new CustomExceptions.ResourceNotFoundException("Usuário", "ID", user.getId());
      }
   }

   @ResponseStatus(HttpStatus.OK)
   @GetMapping("/list")
   public ResponseEntity<List<ClientRecord.clientListDTO>> listByUserId(@RequestParam("userId") Long userId) {
      try {
         List<ClientRecord.clientListDTO> clients = clientService.getClientsByUserId(userId);
         if (clients.isEmpty()) {
            throw new CustomExceptions.ResourceNotFoundException("Clientes", "userId", userId);
         }
         return ResponseEntity.ok(clients);
      } catch (CustomExceptions.ResourceNotFoundException e) {
         throw e;
      } catch (Exception e) {
         throw new CustomExceptions.InvalidDataException("Erro ao listar clientes: " + e.getMessage());
      }
   }
}