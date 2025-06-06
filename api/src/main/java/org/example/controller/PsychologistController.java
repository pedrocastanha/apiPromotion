package org.example.controller;

import org.example.dto.*;
import org.example.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/psicologa")
public class PsychologistController {
    @Autowired
    private AppointmentService appointmentService;
    
    // Listar agenda do dia
    @GetMapping("/agenda/hoje")
    public ResponseEntity<List<AppointmentResponse>> listarAgendaHoje(Principal principal) {
        String emailPsicologa = principal.getName();
        List<AppointmentResponse> agenda = appointmentService.listarAgendaDoDia(emailPsicologa);
        return ResponseEntity.ok(agenda);
    }
    
    // Listar agenda por data
    @GetMapping("/agenda")
    public ResponseEntity<List<AppointmentResponse>> listarAgendaPorData(
            Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        String emailPsicologa = principal.getName();
        List<AppointmentResponse> agenda = appointmentService.listarAgendaPorData(emailPsicologa, data);
        return ResponseEntity.ok(agenda);
    }
    
    // Cancelar consulta
    @PostMapping("/cancelar")
    public ResponseEntity<?> cancelarConsulta(Principal principal, 
                                             @RequestBody CancelamentoRequest req) {
        String emailPsicologa = principal.getName();
        appointmentService.cancelarPorPsicologa(emailPsicologa, req.getAgendamentoId(), req.getMotivo());
        return ResponseEntity.ok("Agendamento cancelado com sucesso");
    }
}
