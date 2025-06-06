package org.example.service;

import org.example.dto.AgendamentoRecorrenteRequest; // Assuming this DTO also needs ID refactoring
import org.example.dto.AgendamentoRequest; // Now uses Long IDs
import org.example.dto.AgendamentoResponse; // Now uses Long IDs
import org.example.exception.BusinessException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.*; // Import all models (assuming they use Long IDs)
import org.example.repository.AgendamentoRepository;
import org.example.repository.ClinicaRepository;
import org.example.repository.ProcedimentoRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled; // Keep commented for now
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
// import java.time.format.DateTimeFormatter; // Not used currently
// import java.time.temporal.ChronoUnit; // Not used currently
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClinicaRepository clinicaRepository;
    @Autowired
    private ProcedimentoRepository procedimentoRepository;
    @Autowired
    private NotificacaoService notificacaoService;
    // @Autowired
    // private ClinicaConfig clinicaConfig; // Removed dependency on static config

    // --- Helper Methods --- 

    // Updated converter to use Long IDs from Agendamento and populate Long IDs in AgendamentoResponse
    private AgendamentoResponse converterParaDTO(Agendamento agendamento) {
        if (agendamento == null) return null;
        
        // Basic conversion, assumes related entities are loaded or fetched as needed.
        // Consider MapStruct for more complex/robust mapping.
        User paciente = agendamento.getPaciente();
        User profissional = agendamento.getProfissional();
        Clinica clinica = agendamento.getClinica();
        Procedimento procedimento = agendamento.getProcedimento();
        User criadoPor = agendamento.getCriadoPorUsuario();
        User canceladoPor = agendamento.getCanceladoPorUsuario();

        return new AgendamentoResponse(
            agendamento.getId(), // Long
            clinica != null ? clinica.getId() : null, // Long
            clinica != null ? clinica.getNomeFantasia() : "N/A",
            paciente != null ? paciente.getId() : null, // Long
            paciente != null ? paciente.getNome() : "N/A",
            profissional != null ? profissional.getId() : null, // Long
            profissional != null ? profissional.getNome() : "N/A",
            procedimento != null ? procedimento.getId() : null, // Long
            procedimento != null ? procedimento.getNome() : null,
            agendamento.getDataHoraInicio(),
            agendamento.getDataHoraFim(),
            agendamento.getStatus(),
            agendamento.getObservacoes(),
            agendamento.getCreatedAt(),
            agendamento.getUpdatedAt(),
            criadoPor != null ? criadoPor.getId() : null, // Long
            criadoPor != null ? criadoPor.getNome() : null,
            agendamento.getDataCancelamento(),
            agendamento.getMotivoCancelamento(),
            canceladoPor != null ? canceladoPor.getId() : null, // Long
            canceladoPor != null ? canceladoPor.getNome() : null
        );
    }

    // Updated helper methods to use Long IDs
    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));
    }

    private Clinica findClinicaByIdOrThrow(Long clinicaId) {
        return clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com ID: " + clinicaId));
    }

    private Agendamento findAgendamentoByIdOrThrow(Long agendamentoId) {
        return agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com ID: " + agendamentoId));
    }
    
    private Procedimento findProcedimentoByIdOrThrow(Long procedimentoId) {
        return procedimentoRepository.findById(procedimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento não encontrado com ID: " + procedimentoId));
    }

    // --- Core Agendamento Logic --- 

    @Transactional
    // Updated method signature to use Long IDs and AgendamentoRequest DTO
    // Assuming criadoPorUsuarioId is obtained from security context or passed differently
    public AgendamentoResponse criarAgendamento(Long clinicaId, Long pacienteId, Long criadoPorUsuarioId, AgendamentoRequest req) {
        Clinica clinica = findClinicaByIdOrThrow(clinicaId);
        User paciente = findUserByIdOrThrow(pacienteId);
        User profissional = findUserByIdOrThrow(req.getProfissionalId()); // Long ID from request
        User criador = findUserByIdOrThrow(criadoPorUsuarioId); // User creating the appointment (Long ID)

        // Validate roles
        if (paciente.getRole() != UserRole.PACIENTE) {
            throw new BusinessException("Usuário fornecido não é um paciente.");
        }
        if (profissional.getRole() != UserRole.PROFISSIONAL) {
            throw new BusinessException("Usuário fornecido não é um profissional.");
        }
        // TODO: Add validation for criador role (e.g., DONO_CLINICA, ATENDENTE, PROFISSIONAL, PACIENTE)

        // Validate if professional belongs to the clinic
        if (profissional.getClinica() == null || !profissional.getClinica().getId().equals(clinicaId)) {
             throw new BusinessException("Profissional não pertence à clínica especificada.");
        }
        // Optionally validate if patient belongs to the clinic or link them
        if (paciente.getClinica() == null) {
            paciente.setClinica(clinica);
            // userRepository.save(paciente); // Consider if this update should happen here
        } else if (!paciente.getClinica().getId().equals(clinicaId)) {
            // Handle case where patient exists but is linked to another clinic - depends on business rules
             // throw new BusinessException("Paciente pertence a outra clínica.");
        }

        OffsetDateTime inicio = req.getDataHoraInicio(); // Assuming DTO provides OffsetDateTime
        
        // Get duration dynamically
        int duracaoMinutos = profissional.getProfissionalInfo() != null ? 
                             profissional.getProfissionalInfo().getDuracaoConsultaPadraoMin() : 60; // Default if no info
        
        Procedimento procedimento = null;
        if (req.getProcedimentoId() != null) {
            procedimento = findProcedimentoByIdOrThrow(req.getProcedimentoId()); // Long ID from request
            if (procedimento.getClinica() == null || !procedimento.getClinica().getId().equals(clinicaId)) {
                 throw new BusinessException("Procedimento não pertence à clínica especificada.");
            }
            duracaoMinutos = procedimento.getDuracaoEstimadaMin(); // Use procedure duration if provided
        }
        
        OffsetDateTime fim = inicio.plusMinutes(duracaoMinutos);

        // Check for overlapping appointments (using Long IDs)
        if (agendamentoRepository.existsOverlappingAppointmentForProfessional(profissional.getId(), inicio, fim)) {
            throw new BusinessException("Profissional já possui um agendamento neste horário.");
        }
        if (agendamentoRepository.existsOverlappingAppointmentForPaciente(paciente.getId(), inicio, fim)) {
            throw new BusinessException("Paciente já possui um agendamento neste horário.");
        }
        // TODO: Check against professional's working hours and exceptions

        Agendamento agendamento = new Agendamento();
        agendamento.setClinica(clinica);
        agendamento.setPaciente(paciente);
        agendamento.setProfissional(profissional);
        agendamento.setCriadoPorUsuario(criador);
        agendamento.setDataHoraInicio(inicio);
        agendamento.setDataHoraFim(fim);
        agendamento.setStatus(AgendamentoStatus.AGENDADO);
        agendamento.setObservacoes(req.getObservacoes());
        agendamento.setRecorrencia(AgendamentoRecorrencia.UNICO);
        agendamento.setProcedimento(procedimento); // Set procedure if applicable

        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);

        // --- Send Notifications --- 
        notificacaoService.criarNotificacaoConfirmacaoAgendamento(savedAgendamento);

        return converterParaDTO(savedAgendamento);
    }

    // TODO: Refactor agendarRecorrente similarly using Long IDs, OffsetDateTime, NotificacaoService, etc.
    @Transactional
    public List<AgendamentoResponse> agendarRecorrente(Long clinicaId, Long pacienteId, Long criadoPorUsuarioId, AgendamentoRecorrenteRequest req) {
        // ... Implementation needed ...
        // Similar logic to criarAgendamento but looping based on recorrencia
        // Validate users (Long IDs), clinic, professional, check overlaps for each instance
        // Save multiple Agendamento entities, potentially linking them via recorrencia_id_pai (Long)
        // Send notification only for the first one or a summary
        throw new UnsupportedOperationException("Agendamento recorrente não implementado ainda.");
    }

    // --- Read Operations --- 

    @Transactional(readOnly = true)
    // Updated to use Long ID
    public List<AgendamentoResponse> listarPorPaciente(Long pacienteId) {
        // TODO: Add security check to ensure the caller can view this patient's appointments
        findUserByIdOrThrow(pacienteId); // Validate user exists
        return agendamentoRepository.findByPacienteId(pacienteId).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    // Updated to use Long ID
    public List<AgendamentoResponse> listarPorProfissional(Long profissionalId) {
         // TODO: Add security check to ensure the caller can view this professional's appointments
         findUserByIdOrThrow(profissionalId); // Validate user exists
        return agendamentoRepository.findByProfissionalId(profissionalId).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    // Updated to use Long ID
    public List<AgendamentoResponse> listarAgendaProfissionalPorData(Long profissionalId, LocalDate data) {
        // TODO: Add security check
        findUserByIdOrThrow(profissionalId); // Validate user exists
        // TODO: Handle TimeZone correctly based on clinic/user settings
        OffsetDateTime inicioDia = data.atStartOfDay().atOffset(ZoneOffset.UTC); // Example: Use UTC
        OffsetDateTime fimDia = inicioDia.plusDays(1);

        List<AgendamentoStatus> statuses = List.of(AgendamentoStatus.AGENDADO, AgendamentoStatus.CONFIRMADO);
        
        // Using repository method with Long ID for professional
        return agendamentoRepository.findByProfissionalIdAndDataHoraInicioBetweenAndStatusInOrderByDataHoraInicioAsc(
                profissionalId, inicioDia, fimDia, statuses)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    // --- Cancellation Logic --- 

    @Transactional
    // Updated to use Long IDs
    public void cancelarPorPaciente(Long pacienteId, Long agendamentoId, String motivo) {
        User paciente = findUserByIdOrThrow(pacienteId);
        Agendamento agendamento = findAgendamentoByIdOrThrow(agendamentoId);

        // Verify ownership
        if (agendamento.getPaciente() == null || !agendamento.getPaciente().getId().equals(pacienteId)) {
            throw new BusinessException("Usuário não tem permissão para cancelar este agendamento.");
        }

        // Check cancellation window (e.g., 24 hours)
        // TODO: Make cancellation window configurable per clinic
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC); // Example: Use UTC
        OffsetDateTime limiteParaCancelar = agendamento.getDataHoraInicio().minusHours(24);

        if (agora.isAfter(limiteParaCancelar)) {
            throw new BusinessException("Não é possível cancelar consultas com menos de 24 horas de antecedência.");
        }
        
        if (agendamento.getStatus() == AgendamentoStatus.CANCELADO_PACIENTE || agendamento.getStatus() == AgendamentoStatus.CANCELADO_CLINICA) {
             throw new BusinessException("Agendamento já está cancelado.");
        }
        if (agendamento.getStatus() == AgendamentoStatus.REALIZADO || agendamento.getStatus() == AgendamentoStatus.NAO_COMPARECEU) {
             throw new BusinessException("Não é possível cancelar um agendamento que já ocorreu ou foi marcado como não compareceu.");
        }

        // Update status and details
        agendamento.setStatus(AgendamentoStatus.CANCELADO_PACIENTE);
        agendamento.setMotivoCancelamento(motivo != null ? motivo : "Cancelado pelo paciente.");
        agendamento.setDataCancelamento(agora);
        agendamento.setCanceladoPorUsuario(paciente);
        agendamentoRepository.save(agendamento);

        // Notify professional
        notificacaoService.criarNotificacaoCancelamentoParaProfissional(agendamento);
    }

    @Transactional
    // Updated to use Long IDs
    public void cancelarPorStaff(Long staffUserId, Long agendamentoId, String motivo) {
        User staff = findUserByIdOrThrow(staffUserId);
        Agendamento agendamento = findAgendamentoByIdOrThrow(agendamentoId);

        // Verify permission (Dono, Atendente of the clinic, or the Profissional of the appointment)
        boolean hasPermission = false;
        if (staff.getRole() == UserRole.DONO_CLINICA || staff.getRole() == UserRole.ATENDENTE) {
            if (staff.getClinica() != null && agendamento.getClinica() != null && staff.getClinica().getId().equals(agendamento.getClinica().getId())) {
                hasPermission = true;
            }
        } else if (staff.getRole() == UserRole.PROFISSIONAL) {
            if (agendamento.getProfissional() != null && agendamento.getProfissional().getId().equals(staffUserId)) {
                hasPermission = true;
            }
        }

        if (!hasPermission) {
            throw new BusinessException("Usuário não tem permissão para cancelar este agendamento.");
        }
        
        if (agendamento.getStatus() == AgendamentoStatus.CANCELADO_PACIENTE || agendamento.getStatus() == AgendamentoStatus.CANCELADO_CLINICA) {
             throw new BusinessException("Agendamento já está cancelado.");
        }
        if (agendamento.getStatus() == AgendamentoStatus.REALIZADO || agendamento.getStatus() == AgendamentoStatus.NAO_COMPARECEU) {
             throw new BusinessException("Não é possível cancelar um agendamento que já ocorreu ou foi marcado como não compareceu.");
        }

        // Update status and details
        agendamento.setStatus(AgendamentoStatus.CANCELADO_CLINICA);
        agendamento.setMotivoCancelamento(motivo != null ? motivo : "Cancelado pela clínica.");
        agendamento.setDataCancelamento(OffsetDateTime.now(ZoneOffset.UTC)); // Example: Use UTC
        agendamento.setCanceladoPorUsuario(staff);
        agendamentoRepository.save(agendamento);

        // Notify Paciente
        notificacaoService.criarNotificacaoCancelamentoParaPaciente(agendamento);
    }

    // --- Scheduled Tasks (Keep commented out unless needed and fully refactored) --- 

    // @Scheduled(cron = "0 0 7 * * ?") // Example: Run daily at 7 AM UTC
    // public void confirmarConsultasDoDia() {
    //     // ... Find appointments for the day using Long IDs and OffsetDateTime ...
    //     // ... Send confirmation notifications via NotificacaoService ...
    // }

    // @Scheduled(cron = "0 0 8 * * ?") // Example: Run daily at 8 AM UTC
    // public void enviarLembretesConsultasAmanha() {
    //     // ... Find appointments for tomorrow using Long IDs and OffsetDateTime ...
    //     // ... Send reminder notifications via NotificacaoService ...
    // }

}

