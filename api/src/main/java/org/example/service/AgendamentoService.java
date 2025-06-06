package org.example.service;

import org.example.dto.AgendamentoRecorrenteRequest;
import org.example.dto.AgendamentoRequest;
import org.example.dto.AgendamentoResponse;
import org.example.exception.BusinessException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.*;
import org.example.repository.AgendamentoRepository;
import org.example.repository.ClinicaRepository;
import org.example.repository.ProcedimentoRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    // --- Helper Methods --- 

    private AgendamentoResponse converterParaDTO(Agendamento agendamento) {
        if (agendamento == null) return null;
        User paciente = agendamento.getPaciente();
        User profissional = agendamento.getProfissional();
        Clinica clinica = agendamento.getClinica();
        Procedimento procedimento = agendamento.getProcedimento();
        User criadoPor = agendamento.getCriadoPorUsuario();
        User canceladoPor = agendamento.getCanceladoPorUsuario();
        Agendamento pai = agendamento.getRecorrenciaPai(); // Get parent appointment

        return new AgendamentoResponse(
            agendamento.getId(),
            clinica != null ? clinica.getId() : null,
            clinica != null ? clinica.getNomeFantasia() : "N/A",
            paciente != null ? paciente.getId() : null,
            paciente != null ? paciente.getNome() : "N/A",
            profissional != null ? profissional.getId() : null,
            profissional != null ? profissional.getNome() : "N/A",
            procedimento != null ? procedimento.getId() : null,
            procedimento != null ? procedimento.getNome() : null,
            agendamento.getDataHoraInicio(),
            agendamento.getDataHoraFim(),
            agendamento.getStatus(),
            agendamento.getObservacoes(),
            agendamento.getRecorrencia(), // Include recurrence type
            pai != null ? pai.getId() : null, // Include parent ID
            agendamento.getCreatedAt(),
            agendamento.getUpdatedAt(),
            criadoPor != null ? criadoPor.getId() : null,
            criadoPor != null ? criadoPor.getNome() : null,
            agendamento.getDataCancelamento(),
            agendamento.getMotivoCancelamento(),
            canceladoPor != null ? canceladoPor.getId() : null,
            canceladoPor != null ? canceladoPor.getNome() : null
        );
    }

    private User findUserByIdOrThrow(Long userId, UserRole expectedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));
        if (expectedRole != null && user.getRole() != expectedRole) {
            throw new BusinessException("Usuário com ID " + userId + " não possui o papel esperado: " + expectedRole);
        }
         if (!user.isAtivo()) {
            throw new BusinessException("Usuário com ID " + userId + " está inativo.");
        }
        return user;
    }

    private Clinica findClinicaByIdOrThrow(Long clinicaId) {
        Clinica clinica = clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com ID: " + clinicaId));
        if (!clinica.isAtiva()) {
             throw new BusinessException("Clínica com ID " + clinicaId + " está inativa.");
        }
        return clinica;
    }

    private Agendamento findAgendamentoByIdOrThrow(Long agendamentoId) {
        return agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com ID: " + agendamentoId));
    }
    
    private Procedimento findProcedimentoByIdOrThrow(Long procedimentoId, Long expectedClinicaId) {
        Procedimento procedimento = procedimentoRepository.findById(procedimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento não encontrado com ID: " + procedimentoId));
        if (procedimento.getClinica() == null || !procedimento.getClinica().getId().equals(expectedClinicaId)) {
             throw new BusinessException("Procedimento ID " + procedimentoId + " não pertence à clínica ID " + expectedClinicaId);
        }
        return procedimento;
    }

    private void validarPermissaoCriacaoAgendamento(User criador, Clinica clinica) {
        // Dono da clínica, Atendente da clínica, ou o próprio Profissional podem criar
        boolean hasPermission = false;
        if (criador.getRole() == UserRole.DONO_CLINICA || criador.getRole() == UserRole.ATENDENTE) {
            if (criador.getClinica() != null && criador.getClinica().getId().equals(clinica.getId())) {
                hasPermission = true;
            }
        } else if (criador.getRole() == UserRole.ADMIN) { // Admin pode criar em qualquer clínica
             hasPermission = true;
        }
        // Adicionar lógica se paciente ou profissional podem criar seus próprios agendamentos

        if (!hasPermission) {
            throw new BusinessException("Usuário " + criador.getEmail() + " não tem permissão para criar agendamentos nesta clínica.");
        }
    }
    
    private void validarProfissionalClinica(User profissional, Clinica clinica) {
         if (profissional.getClinica() == null || !profissional.getClinica().getId().equals(clinica.getId())) {
             throw new BusinessException("Profissional " + profissional.getNome() + " (ID: " + profissional.getId() + ") não pertence à clínica " + clinica.getNomeFantasia() + " (ID: " + clinica.getId() + ").");
        }
    }
    
    private void validarPacienteClinica(User paciente, Clinica clinica) {
         // Regra de negócio: Paciente pode ser atendido em qualquer clínica ou precisa estar vinculado?
         // Por enquanto, vamos permitir, mas podemos adicionar validação se necessário.
         // Exemplo: Vincular paciente à clínica no primeiro agendamento
         if (paciente.getClinica() == null && clinica != null) {
             paciente.setClinica(clinica);
             // userRepository.save(paciente); // Salvar a associação pode ser feito aqui ou em outro ponto
         } 
         // else if (paciente.getClinica() != null && !paciente.getClinica().getId().equals(clinica.getId())) {
         //     throw new BusinessException("Paciente " + paciente.getNome() + " já está associado a outra clínica.");
         // }
    }
    
    private void validarConflitoHorario(Long profissionalId, Long pacienteId, OffsetDateTime inicio, OffsetDateTime fim, Long agendamentoIdExcluir) {
         // Verifica conflito para o profissional
        if (agendamentoRepository.existsOverlappingAppointmentForProfessional(profissionalId, inicio, fim, agendamentoIdExcluir)) {
            throw new BusinessException("Conflito de horário: Profissional (ID: " + profissionalId + ") já possui um agendamento neste período.");
        }
        // Verifica conflito para o paciente
        if (agendamentoRepository.existsOverlappingAppointmentForPaciente(pacienteId, inicio, fim, agendamentoIdExcluir)) {
            throw new BusinessException("Conflito de horário: Paciente (ID: " + pacienteId + ") já possui um agendamento neste período.");
        }
        // TODO: Adicionar validação contra horário de trabalho e exceções do profissional
    }
    
    private int calcularDuracaoMinutos(User profissional, Procedimento procedimento) {
        int duracaoMinutos = 60; // Duração padrão
        if (procedimento != null && procedimento.getDuracaoEstimadaMin() > 0) {
            duracaoMinutos = procedimento.getDuracaoEstimadaMin();
        } else if (profissional.getProfissionalInfo() != null && profissional.getProfissionalInfo().getDuracaoConsultaPadraoMin() > 0) {
            duracaoMinutos = profissional.getProfissionalInfo().getDuracaoConsultaPadraoMin();
        }
        return duracaoMinutos;
    }

    // --- Core Agendamento Logic --- 

    @Transactional
    public AgendamentoResponse criarAgendamentoUnico(Long clinicaId, Long criadoPorUsuarioId, AgendamentoRequest req) {
        Clinica clinica = findClinicaByIdOrThrow(clinicaId);
        User criador = findUserByIdOrThrow(criadoPorUsuarioId, null); // Valida se criador existe
        validarPermissaoCriacaoAgendamento(criador, clinica);

        User paciente = findUserByIdOrThrow(req.getPacienteId(), UserRole.PACIENTE);
        User profissional = findUserByIdOrThrow(req.getProfissionalId(), UserRole.PROFISSIONAL);
        validarProfissionalClinica(profissional, clinica);
        validarPacienteClinica(paciente, clinica);
        
        Procedimento procedimento = null;
        if (req.getProcedimentoId() != null) {
            procedimento = findProcedimentoByIdOrThrow(req.getProcedimentoId(), clinicaId);
        }

        OffsetDateTime inicio = req.getDataHoraInicio(); // Assume OffsetDateTime
        int duracaoMinutos = calcularDuracaoMinutos(profissional, procedimento);
        OffsetDateTime fim = inicio.plusMinutes(duracaoMinutos);

        validarConflitoHorario(profissional.getId(), paciente.getId(), inicio, fim, null);

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
        agendamento.setProcedimento(procedimento);

        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);

        notificacaoService.criarNotificacaoConfirmacaoAgendamento(savedAgendamento);

        return converterParaDTO(savedAgendamento);
    }

    @Transactional
    public List<AgendamentoResponse> criarAgendamentoRecorrente(Long clinicaId, Long criadoPorUsuarioId, AgendamentoRecorrenteRequest req) {
        Clinica clinica = findClinicaByIdOrThrow(clinicaId);
        User criador = findUserByIdOrThrow(criadoPorUsuarioId, null);
        validarPermissaoCriacaoAgendamento(criador, clinica);

        User paciente = findUserByIdOrThrow(req.getPacienteId(), UserRole.PACIENTE);
        User profissional = findUserByIdOrThrow(req.getProfissionalId(), UserRole.PROFISSIONAL);
        validarProfissionalClinica(profissional, clinica);
        validarPacienteClinica(paciente, clinica);

        Procedimento procedimento = null;
        if (req.getProcedimentoId() != null) {
            procedimento = findProcedimentoByIdOrThrow(req.getProcedimentoId(), clinicaId);
        }

        OffsetDateTime dataInicioRecorrencia = req.getDataHoraInicioPrimeiro();
        OffsetDateTime dataFimRecorrencia = req.getDataFimRecorrencia(); // Pode ser nulo se usar numeroOcorrencias
        int numeroOcorrencias = req.getNumeroOcorrencias() != null ? req.getNumeroOcorrencias() : 0;
        AgendamentoRecorrencia frequencia = req.getFrequencia();
        
        if (frequencia == null || frequencia == AgendamentoRecorrencia.UNICO) {
            throw new BusinessException("Frequência inválida para agendamento recorrente.");
        }
        if (dataFimRecorrencia == null && numeroOcorrencias <= 0) {
             throw new BusinessException("É necessário fornecer uma data final ou um número de ocorrências para agendamentos recorrentes.");
        }
        if (dataFimRecorrencia != null && numeroOcorrencias > 0) {
             throw new BusinessException("Forneça apenas a data final OU o número de ocorrências, não ambos.");
        }
        if (dataFimRecorrencia != null && dataFimRecorrencia.isBefore(dataInicioRecorrencia)) {
            throw new BusinessException("A data final da recorrência não pode ser anterior à data de início.");
        }
        // Limitar número máximo de ocorrências para evitar loops infinitos/abusos
        final int MAX_OCORRENCIAS = 52; // Ex: Limite de 1 ano para recorrências semanais
        if (numeroOcorrencias > MAX_OCORRENCIAS) {
             throw new BusinessException("O número máximo de ocorrências permitido é " + MAX_OCORRENCIAS);
        }

        List<Agendamento> agendamentosCriados = new ArrayList<>();
        OffsetDateTime dataIteracao = dataInicioRecorrencia;
        int ocorrenciasCriadas = 0;
        Agendamento agendamentoPai = null;

        int duracaoMinutos = calcularDuracaoMinutos(profissional, procedimento);

        while ((dataFimRecorrencia != null && !dataIteracao.isAfter(dataFimRecorrencia)) || 
               (numeroOcorrencias > 0 && ocorrenciasCriadas < numeroOcorrencias)) {
            
            if (ocorrenciasCriadas >= MAX_OCORRENCIAS && numeroOcorrencias <= 0) { // Segurança extra caso use data fim
                 System.err.println("Atingido limite máximo de ocorrências (" + MAX_OCORRENCIAS + ") para recorrência com data final.");
                 break;
            }

            OffsetDateTime inicioIteracao = dataIteracao;
            OffsetDateTime fimIteracao = inicioIteracao.plusMinutes(duracaoMinutos);

            try {
                // Validar conflito para esta ocorrência específica
                validarConflitoHorario(profissional.getId(), paciente.getId(), inicioIteracao, fimIteracao, null);
                // TODO: Validar horário de trabalho do profissional para esta data/hora

                Agendamento agendamento = new Agendamento();
                agendamento.setClinica(clinica);
                agendamento.setPaciente(paciente);
                agendamento.setProfissional(profissional);
                agendamento.setCriadoPorUsuario(criador);
                agendamento.setDataHoraInicio(inicioIteracao);
                agendamento.setDataHoraFim(fimIteracao);
                agendamento.setStatus(AgendamentoStatus.AGENDADO);
                agendamento.setObservacoes(req.getObservacoes()); // Pode querer diferenciar observações
                agendamento.setRecorrencia(frequencia);
                agendamento.setProcedimento(procedimento);
                
                if (agendamentoPai != null) {
                    agendamento.setRecorrenciaPai(agendamentoPai);
                }

                Agendamento savedAgendamento = agendamentoRepository.save(agendamento);
                agendamentosCriados.add(savedAgendamento);
                ocorrenciasCriadas++;
                
                // Define o primeiro agendamento criado como pai dos subsequentes
                if (agendamentoPai == null) {
                    agendamentoPai = savedAgendamento;
                    // Salva novamente o pai para garantir que o ID está lá antes de ser referenciado
                    // (Embora o save retorne o objeto com ID, uma referência explícita pode ser mais segura)
                    // agendamentoRepository.save(agendamentoPai); 
                }

            } catch (BusinessException e) {
                // Logar ou informar sobre a falha na criação desta ocorrência específica
                System.err.println("Falha ao criar ocorrência em " + inicioIteracao + ": " + e.getMessage());
                // Decide se continua ou para. Por enquanto, vamos pular esta ocorrência.
            }

            // Calcula a próxima data
            switch (frequencia) {
                case SEMANAL:   dataIteracao = dataIteracao.plusWeeks(1); break;
                case QUINZENAL: dataIteracao = dataIteracao.plusWeeks(2); break;
                case MENSAL:    dataIteracao = dataIteracao.plusMonths(1); break;
                default: throw new IllegalStateException("Frequência inesperada: " + frequencia);
            }
        }
        
        if (agendamentosCriados.isEmpty()) {
            throw new BusinessException("Nenhuma ocorrência de agendamento pôde ser criada com os parâmetros fornecidos.");
        }

        // Enviar notificação apenas para o primeiro agendamento criado (o pai)
        if (agendamentoPai != null) {
             notificacaoService.criarNotificacaoConfirmacaoAgendamento(agendamentoPai);
        }

        return agendamentosCriados.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    // --- Read Operations --- 

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarAgendaDaClinica(Long clinicaId, Long usuarioLogadoId, LocalDate dataInicio, LocalDate dataFim) {
        Clinica clinica = findClinicaByIdOrThrow(clinicaId);
        User usuarioLogado = findUserByIdOrThrow(usuarioLogadoId, null);

        // Validar permissão (Dono da Clínica ou Admin)
        boolean hasPermission = false;
        if (usuarioLogado.getRole() == UserRole.ADMIN) {
            hasPermission = true;
        } else if (usuarioLogado.getRole() == UserRole.DONO_CLINICA || usuarioLogado.getRole() == UserRole.ATENDENTE) {
             if (usuarioLogado.getClinica() != null && usuarioLogado.getClinica().getId().equals(clinicaId)) {
                hasPermission = true;
            }
        }
        if (!hasPermission) {
             throw new BusinessException("Usuário não tem permissão para visualizar a agenda completa desta clínica.");
        }

        if (dataInicio == null || dataFim == null || dataFim.isBefore(dataInicio)) {
            throw new BusinessException("Datas de início e fim inválidas para consulta da agenda.");
        }

        // TODO: Considerar TimeZone da clínica
        OffsetDateTime inicioPeriodo = dataInicio.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime fimPeriodo = dataFim.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC); // Fim exclusivo

        List<AgendamentoStatus> statusesVisiveis = List.of(AgendamentoStatus.AGENDADO, AgendamentoStatus.CONFIRMADO, AgendamentoStatus.REALIZADO, AgendamentoStatus.NAO_COMPARECEU);

        return agendamentoRepository.findByClinicaIdAndDataHoraInicioBetweenAndStatusInOrderByDataHoraInicioAsc(
                clinicaId, inicioPeriodo, fimPeriodo, statusesVisiveis)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AgendamentoResponse buscarAgendamentoPorId(Long agendamentoId, Long usuarioLogadoId) {
        Agendamento agendamento = findAgendamentoByIdOrThrow(agendamentoId);
        User usuarioLogado = findUserByIdOrThrow(usuarioLogadoId, null);
        Clinica clinicaAgendamento = agendamento.getClinica();

        // Validar permissão de visualização
        boolean hasPermission = false;
        if (usuarioLogado.getRole() == UserRole.ADMIN) {
            hasPermission = true;
        } else if (usuarioLogado.getId().equals(agendamento.getPaciente().getId())) { // Próprio paciente
             hasPermission = true;
        } else if (usuarioLogado.getId().equals(agendamento.getProfissional().getId())) { // Próprio profissional
             hasPermission = true;
        } else if (clinicaAgendamento != null && (usuarioLogado.getRole() == UserRole.DONO_CLINICA || usuarioLogado.getRole() == UserRole.ATENDENTE)) {
             if (usuarioLogado.getClinica() != null && usuarioLogado.getClinica().getId().equals(clinicaAgendamento.getId())) {
                hasPermission = true;
            }
        }

        if (!hasPermission) {
            throw new BusinessException("Usuário não tem permissão para visualizar este agendamento.");
        }

        return converterParaDTO(agendamento);
    }

    // --- Cancellation Logic --- 

    @Transactional
    public void cancelarAgendamento(Long agendamentoId, Long usuarioCancelandoId, String motivo) {
        Agendamento agendamento = findAgendamentoByIdOrThrow(agendamentoId);
        User usuarioCancelando = findUserByIdOrThrow(usuarioCancelandoId, null);
        Clinica clinicaAgendamento = agendamento.getClinica();

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new BusinessException("O motivo do cancelamento é obrigatório.");
        }

        // Verificar se já está cancelado ou finalizado
        if (agendamento.getStatus() == AgendamentoStatus.CANCELADO_PACIENTE || agendamento.getStatus() == AgendamentoStatus.CANCELADO_CLINICA) {
             throw new BusinessException("Agendamento (ID: " + agendamentoId + ") já está cancelado.");
        }
        if (agendamento.getStatus() == AgendamentoStatus.REALIZADO || agendamento.getStatus() == AgendamentoStatus.NAO_COMPARECEU) {
             throw new BusinessException("Não é possível cancelar um agendamento que já ocorreu ou foi marcado como não compareceu.");
        }

        boolean canceladoPelaClinica = false;
        boolean hasPermission = false;

        // 1. Admin pode cancelar qualquer agendamento
        if (usuarioCancelando.getRole() == UserRole.ADMIN) {
            hasPermission = true;
            canceladoPelaClinica = true; // Assume cancelamento pela clínica quando admin cancela
        }
        // 2. Dono/Atendente da clínica do agendamento pode cancelar
        else if (clinicaAgendamento != null && (usuarioCancelando.getRole() == UserRole.DONO_CLINICA || usuarioCancelando.getRole() == UserRole.ATENDENTE)) {
            if (usuarioCancelando.getClinica() != null && usuarioCancelando.getClinica().getId().equals(clinicaAgendamento.getId())) {
                hasPermission = true;
                canceladoPelaClinica = true;
            }
        }
        // 3. Profissional do agendamento pode cancelar
        else if (usuarioCancelando.getId().equals(agendamento.getProfissional().getId())) {
             hasPermission = true;
             canceladoPelaClinica = true;
        }
        // 4. Paciente do agendamento pode cancelar (com regras de antecedência)
        else if (usuarioCancelando.getId().equals(agendamento.getPaciente().getId())) {
            // TODO: Tornar a antecedência configurável por clínica
            final int HORAS_ANTECEDENCIA_MINIMA = 24;
            OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC); // Usar UTC ou Timezone da Clínica
            OffsetDateTime limiteParaCancelar = agendamento.getDataHoraInicio().minusHours(HORAS_ANTECEDENCIA_MINIMA);

            if (agora.isAfter(limiteParaCancelar)) {
                throw new BusinessException("Não é possível cancelar consultas com menos de " + HORAS_ANTECEDENCIA_MINIMA + " horas de antecedência.");
            }
            hasPermission = true;
            canceladoPelaClinica = false;
        }

        if (!hasPermission) {
            throw new BusinessException("Usuário (ID: " + usuarioCancelandoId + ") não tem permissão para cancelar este agendamento (ID: " + agendamentoId + ").");
        }

        // Atualizar status e detalhes
        agendamento.setStatus(canceladoPelaClinica ? AgendamentoStatus.CANCELADO_CLINICA : AgendamentoStatus.CANCELADO_PACIENTE);
        agendamento.setMotivoCancelamento(motivo);
        agendamento.setDataCancelamento(OffsetDateTime.now(ZoneOffset.UTC)); // Usar UTC ou Timezone da Clínica
        agendamento.setCanceladoPorUsuario(usuarioCancelando);
        agendamentoRepository.save(agendamento);

        // Disparar notificações
        if (canceladoPelaClinica) {
            // Clínica/Profissional/Admin cancelou -> Notificar Paciente
            notificacaoService.criarNotificacaoCancelamentoParaPaciente(agendamento);
        } else {
            // Paciente cancelou -> Notificar Profissional (e talvez a clínica/atendente)
            notificacaoService.criarNotificacaoCancelamentoParaProfissional(agendamento);
            // notificacaoService.criarNotificacaoCancelamentoParaClinica(agendamento); // Opcional
        }
        
        // TODO: Lógica para cancelar agendamentos futuros da mesma recorrência, se aplicável
        // if (req.cancelarRecorrenciaFutura && agendamento.getRecorrenciaPai() != null) { ... }
    }
    
     // --- Outras operações (Ex: Confirmar, Marcar como Realizado/Não Compareceu) --- 
     // TODO: Implementar métodos para confirmar agendamento, marcar como realizado, não compareceu, etc.
     // Exemplo:
     /*
     @Transactional
     public void confirmarAgendamento(Long agendamentoId, Long usuarioConfirmandoId) {
         Agendamento agendamento = findAgendamentoByIdOrThrow(agendamentoId);
         User usuarioConfirmando = findUserByIdOrThrow(usuarioConfirmandoId, null);
         // ... (validações de permissão e status) ...
         if (agendamento.getStatus() == AgendamentoStatus.AGENDADO) {
             agendamento.setStatus(AgendamentoStatus.CONFIRMADO);
             agendamentoRepository.save(agendamento);
             // notificacaoService.criarNotificacaoConfirmacao(agendamento); // Opcional
         } else {
             throw new BusinessException("Agendamento não pode ser confirmado no estado atual: " + agendamento.getStatus());
         }
     }
     */

}

