package org.example.controller;

import org.example.dto.*;
import org.example.model.User;
import org.example.service.UserService;
import org.example.config.JwtTokenUtil;
import org.example.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPaciente(@RequestBody RegistrationRequest req) {
        User usuario = new User();
        usuario.setNome(req.getNome());
        usuario.setEmail(req.getEmail());
        usuario.setSenha(req.getSenha());
        usuario.setTelefone(req.getTelefone());
        userService.registerPaciente(usuario);
        return ResponseEntity.ok("Paciente registrado com sucesso");
    }

    // Autenticação (login)
    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody LoginRequest req) throws Exception {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

