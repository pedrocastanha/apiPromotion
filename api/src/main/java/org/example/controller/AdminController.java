package org.example.controller;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    // Cadastro de nova psicóloga (apenas Admin)
    @PostMapping("/psicologa")
    public ResponseEntity<?> registerPsicologa(@RequestBody User usuario) {
        userService.registerPsicologa(usuario);
        return ResponseEntity.ok("Psicóloga registrada");
    }

    // Listar todos os agendamentos (apenas Admin)
    @GetMapping("/agendamentos")
    public ResponseEntity<List<?>> listarTodosAgendamentos() {
        // Implementação que retorna lista de agendamentos (omissão de detalhes)
        return ResponseEntity.ok(List.of());
    }
}

