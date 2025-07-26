package org.example.user;

import org.example.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);

        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginDTO login) {
        Optional<User> optionalUser = userService.findByEmail(login.getEmail());

        User user = optionalUser.get();
        boolean passwordMatches = passwordEncoder.matches(login.getPassword(), user.getPassword());

        if(passwordMatches) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().build();
    }
}
