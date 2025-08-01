package org.example.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.client.Client;
import org.example.domain.client.ClientRecord;
import org.example.domain.client.ClientServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final ClientServiceImpl clientServiceImpl;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }
}
