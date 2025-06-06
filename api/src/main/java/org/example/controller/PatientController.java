package org.example.controller;
import org.example.dto.*;
import org.example.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/paciente")
public class PatientController {
    @Autowired
    private AppointmentService appointmentService;

    // Agendar novo atendimento
    @PostMapping("/agendar")
    public ResponseEntity<AppointmentResponse> agendar(Principal principal,
                                                       @RequestBody AppointmentRequest req) {
        String emailPaciente = principal.getName();
        AppointmentResponse resp = appointmentService.agendar(emailPaciente, req);
        return ResponseEntity.ok(resp);
    }
    
    // Agendar atendimentos recorrentes
    @PostMapping("/agendar-recorrente")
    public ResponseEntity<List<AppointmentResponse>> agendarRecorrente(
            Principal principal,
            @RequestBody AgendamentoRecorrenteRequest req) {
        String emailPaciente = principal.getName();
        List<AppointmentResponse> agendamentos = 
            appointmentService.agendarRecorrente(emailPaciente, req);
        return ResponseEntity.ok(agendamentos);
    }

    // Listar agendamentos do paciente logado
    @GetMapping("/agendamentos")
    public ResponseEntity<List<AppointmentResponse>> listar(Principal principal) {
        String emailPaciente = principal.getName();
        List<AppointmentResponse> list = appointmentService.listarPorPaciente(emailPaciente);
        return ResponseEntity.ok(list);
    }

    // Cancelar agendamento existente (atualizado para usar o novo método com motivo)
    @PostMapping("/cancelar")
    public ResponseEntity<?> cancelarConsulta(Principal principal, 
                                             @RequestBody CancelamentoRequest req) {
        String emailPaciente = principal.getName();
        appointmentService.cancelarPorPaciente(emailPaciente, req.getAgendamentoId(), req.getMotivo());
        return ResponseEntity.ok("Agendamento cancelado com sucesso");
    }
    
    // Método antigo de cancelamento - mantido para compatibilidade, mas marcado como deprecated
    @Deprecated
    @DeleteMapping("/agendamentos/{id}")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        // Redireciona para o novo método, mas sem motivo específico
        appointmentService.cancelar(id);
        return ResponseEntity.ok("Agendamento cancelado");
    }
}
