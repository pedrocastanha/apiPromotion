package org.example.service;
import org.example.config.ClinicaConfig;
import org.example.model.Appointment;
import org.example.model.AppointmentStatus;
import org.example.model.User;
import org.example.model.FrequenciaAgendamento;
import org.example.repository.AppointmentRepository;
import org.example.dto.AppointmentRequest;
import org.example.dto.AppointmentResponse;
import org.example.dto.AgendamentoRecorrenteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TwilioService twilioService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ClinicaConfig clinicaConfig;

    // Agendar novo atendimento
    public AppointmentResponse agendar(String emailPaciente, AppointmentRequest req) {
        User paciente = userService.findByEmail(emailPaciente);
        User psicologa = userService.findById(req.getPsicologaId());
        
        Appointment agendamento = new Appointment();
        agendamento.setPaciente(paciente);
        agendamento.setPsicologa(psicologa);
        agendamento.setHorario(req.getHorario());
        agendamento.setStatus(AppointmentStatus.AGENDADO);
        agendamento.setObservacoes(req.getObservacoes());
        
        appointmentRepository.save(agendamento);

        // Formatar data e hora para exibição
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataHoraFormatada = agendamento.getHorario().format(formatter);
        
        // Envia mensagem pelo WhatsApp via Twilio
        String msg = String.format(
            "Olá %s, seu agendamento está confirmado para %s com a psicóloga %s",
            paciente.getNome(),
            dataHoraFormatada,
            psicologa.getNome()
        );
        twilioService.enviarWhatsApp(paciente.getTelefone(), msg);
        
        // Enviar email de confirmação
        notificarConfirmacaoAgendamento(paciente, agendamento);

        // Retorna DTO de resposta
        return converterParaDTO(agendamento);
    }
    
    // Agendar atendimentos recorrentes
    public List<AppointmentResponse> agendarRecorrente(String emailPaciente, AgendamentoRecorrenteRequest req) {
        User paciente = userService.findByEmail(emailPaciente);
        User psicologa = userService.findById(req.getPsicologaId());
        
        LocalDateTime dataHoraInicial = req.getDataHoraInicial();
        List<Appointment> agendamentos = new ArrayList<>();
        
        // Determinar intervalo baseado na frequência
        int intervalo;
        ChronoUnit unidade;
        
        switch (paciente.getFrequenciaAgendamento()) {
            case SEMANAL:
                intervalo = 1;
                unidade = ChronoUnit.WEEKS;
                break;
            case QUINZENAL:
                intervalo = 2;
                unidade = ChronoUnit.WEEKS;
                break;
            case MENSAL:
                intervalo = 1;
                unidade = ChronoUnit.MONTHS;
                break;
            default: // VARIADO - apenas um agendamento
                intervalo = 0;
                unidade = ChronoUnit.DAYS;
        }
        
        // Criar agendamentos recorrentes
        LocalDateTime dataHora = dataHoraInicial;
        int quantidadeAgendamentos = intervalo > 0 ? req.getQuantidadeRecorrencias() : 1;
        
        for (int i = 0; i < quantidadeAgendamentos; i++) {
            Appointment agendamento = new Appointment();
            agendamento.setPaciente(paciente);
            agendamento.setPsicologa(psicologa);
            agendamento.setHorario(dataHora);
            agendamento.setStatus(AppointmentStatus.AGENDADO);
            agendamento.setObservacoes(req.getObservacoes());
            
            agendamentos.add(appointmentRepository.save(agendamento));
            
            // Calcular próxima data
            if (intervalo > 0) {
                if (unidade == ChronoUnit.WEEKS) {
                    dataHora = dataHora.plusWeeks(intervalo);
                } else if (unidade == ChronoUnit.MONTHS) {
                    dataHora = dataHora.plusMonths(intervalo);
                }
            }
        }
        
        // Enviar confirmação apenas do primeiro agendamento
        if (!agendamentos.isEmpty()) {
            Appointment primeiroAgendamento = agendamentos.get(0);
            
            // Formatar data e hora para exibição
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
            String dataHoraFormatada = primeiroAgendamento.getHorario().format(formatter);
            
            String msg = String.format(
                "Olá %s, seu agendamento está confirmado para %s com a psicóloga %s",
                paciente.getNome(),
                dataHoraFormatada,
                psicologa.getNome()
            );
            twilioService.enviarWhatsApp(paciente.getTelefone(), msg);
            
            // Enviar email de confirmação
            notificarConfirmacaoAgendamento(paciente, primeiroAgendamento);
        }
        
        // Converter para DTOs
        return agendamentos.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    // Listar agendamentos de um paciente
    public List<AppointmentResponse> listarPorPaciente(String emailPaciente) {
        User paciente = userService.findByEmail(emailPaciente);
        return appointmentRepository.findByPaciente(paciente).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    // Listar agenda do dia para psicóloga
    public List<AppointmentResponse> listarAgendaDoDia(String emailPsicologa) {
        User psicologa = userService.findByEmail(emailPsicologa);
        
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fimDia = inicioDia.plusDays(1);
        
        return appointmentRepository.findByPsicologaAndHorarioBetweenAndStatus(
                psicologa, inicioDia, fimDia, AppointmentStatus.AGENDADO)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    // Listar agenda por data para psicóloga
    public List<AppointmentResponse> listarAgendaPorData(String emailPsicologa, LocalDate data) {
        User psicologa = userService.findByEmail(emailPsicologa);
        
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1);
        
        return appointmentRepository.findByPsicologaAndHorarioBetweenAndStatus(
                psicologa, inicioDia, fimDia, AppointmentStatus.AGENDADO)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    // Método legado de cancelamento - mantido para compatibilidade
    @Deprecated
    public void cancelar(Long agendamentoId) {
        Appointment ag = appointmentRepository.findById(agendamentoId)
            .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        ag.setStatus(AppointmentStatus.CANCELADO);
        appointmentRepository.save(ag);

        // Notificar pelo WhatsApp
        String msg = "Seu agendamento do dia " + ag.getHorario() + " foi cancelado.";
        twilioService.enviarWhatsApp(ag.getPaciente().getTelefone(), msg);
    }
    
    // Cancelamento por paciente com validação de 24h e motivo
    public void cancelarPorPaciente(String emailPaciente, Long agendamentoId, String motivo) {
        User paciente = userService.findByEmail(emailPaciente);
        Appointment agendamento = appointmentRepository.findById(agendamentoId)
            .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        
        // Verificar se o paciente é o dono do agendamento
        if (!agendamento.getPaciente().getId().equals(paciente.getId())) {
            throw new RuntimeException("Você não tem permissão para cancelar este agendamento");
        }
        
        // Verificar se o agendamento está a menos de 24h
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limiteParaCancelar = agendamento.getHorario().minusHours(24);
        
        if (agora.isAfter(limiteParaCancelar)) {
            throw new RuntimeException("Não é possível cancelar consultas com menos de 24 horas de antecedência");
        }
        
        // Registrar cancelamento
        agendamento.setStatus(AppointmentStatus.CANCELADO);
        agendamento.setMotivoCancelamento(motivo);
        agendamento.setCanceladoPor("PACIENTE");
        appointmentRepository.save(agendamento);
        
        // Notificar psicóloga
        notificarCancelamentoPsicologa(agendamento);
    }
    
    // Cancelamento por psicóloga com motivo
    public void cancelarPorPsicologa(String emailPsicologa, Long agendamentoId, String motivo) {
        User psicologa = userService.findByEmail(emailPsicologa);
        Appointment agendamento = appointmentRepository.findById(agendamentoId)
            .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        
        // Verificar se a psicóloga é a dona do agendamento
        if (!agendamento.getPsicologa().getId().equals(psicologa.getId())) {
            throw new RuntimeException("Você não tem permissão para cancelar este agendamento");
        }
        
        // Registrar cancelamento
        agendamento.setStatus(AppointmentStatus.CANCELADO);
        agendamento.setMotivoCancelamento(motivo);
        agendamento.setCanceladoPor("PSICOLOGA");
        appointmentRepository.save(agendamento);
        
        // Notificar paciente
        notificarCancelamentoPaciente(agendamento);
    }
    
    private void notificarCancelamentoPaciente(Appointment agendamento) {
        User paciente = agendamento.getPaciente();
        User psicologa = agendamento.getPsicologa();
        
        // Formatar data e hora para exibição
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataHoraFormatada = agendamento.getHorario().format(formatter);
        
        // Enviar WhatsApp
        String mensagemWhatsApp = String.format(
            "Olá %s, sua consulta do dia %s com a psicóloga %s foi cancelada. Motivo: %s",
            paciente.getNome(),
            dataHoraFormatada,
            psicologa.getNome(),
            agendamento.getMotivoCancelamento()
        );
        twilioService.enviarWhatsApp(paciente.getTelefone(), mensagemWhatsApp);
        
        // Enviar Email
        String assunto = "Cancelamento de Consulta - Clínica Psico";
        String corpoHtml = String.format(
            "<h3>Olá, %s</h3>" +
            "<p>Sua consulta agendada para <b>%s</b> com a psicóloga %s foi cancelada.</p>" +
            "<p><b>Motivo do cancelamento:</b> %s</p>" +
            "<p>Para reagendar, entre em contato conosco.</p>",
            paciente.getNome(),
            dataHoraFormatada,
            psicologa.getNome(),
            agendamento.getMotivoCancelamento()
        );
        emailService.enviarEmailHtml(paciente.getEmail(), assunto, corpoHtml);
    }
    
    private void notificarCancelamentoPsicologa(Appointment agendamento) {
        User paciente = agendamento.getPaciente();
        User psicologa = agendamento.getPsicologa();
        
        // Formatar data e hora para exibição
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataHoraFormatada = agendamento.getHorario().format(formatter);
        
        // Enviar WhatsApp
        String mensagemWhatsApp = String.format(
            "Olá %s, o paciente %s cancelou a consulta agendada para %s. Motivo: %s",
            psicologa.getNome(),
            paciente.getNome(),
            dataHoraFormatada,
            agendamento.getMotivoCancelamento()
        );
        twilioService.enviarWhatsApp(psicologa.getTelefone(), mensagemWhatsApp);
        
        // Enviar Email
        String assunto = "Cancelamento de Consulta - Clínica Psico";
        String corpoHtml = String.format(
            "<h3>Olá, %s</h3>" +
            "<p>O paciente %s cancelou a consulta agendada para <b>%s</b>.</p>" +
            "<p><b>Motivo do cancelamento:</b> %s</p>",
            psicologa.getNome(),
            paciente.getNome(),
            dataHoraFormatada,
            agendamento.getMotivoCancelamento()
        );
        emailService.enviarEmailHtml(psicologa.getEmail(), assunto, corpoHtml);
    }

    public void notificarConfirmacaoAgendamento(User paciente, Appointment agendamento) {
        String assunto = "Confirmação de Agendamento - Clínica Psico";
        
        // Formatar data e hora para exibição
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataHoraFormatada = agendamento.getHorario().format(formatter);
        
        String corpoHtml = String.format(
            "<h3>Olá, %s</h3>" +
            "<p>Seu agendamento com a psicóloga %s foi confirmado para <b>%s</b>.</p>" +
            "<p>Endereço da clínica: %s</p>" +
            "<p>Qualquer dúvida, entre em contato conosco.</p>",
            paciente.getNome(),
            agendamento.getPsicologa().getNome(),
            dataHoraFormatada,
            clinicaConfig.getEnderecoCompleto()
        );

        emailService.enviarEmailHtml(paciente.getEmail(), assunto, corpoHtml);
    }
    
    // Método para confirmar consultas do dia
    @Scheduled(cron = "0 0 7 * * ?") // Executa todos os dias às 7h da manhã
    public void confirmarConsultasDoDia() {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fimDia = inicioDia.plusDays(1);
        
        List<Appointment> consultasDoDia = appointmentRepository.findByHorarioBetweenAndStatus(
            inicioDia, fimDia, AppointmentStatus.AGENDADO);
        
        for (Appointment consulta : consultasDoDia) {
            enviarConfirmacaoConsulta(consulta);
        }
    }
    
    private void enviarConfirmacaoConsulta(Appointment agendamento) {
        User paciente = agendamento.getPaciente();
        User psicologa = agendamento.getPsicologa();
        
        // Formatar data e hora para exibição
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataHoraFormatada = agendamento.getHorario().format(formatter);
        
        // Obter localização da clínica
        String localizacaoClinica = clinicaConfig.getEnderecoCompleto();
        
        // Enviar WhatsApp
        String mensagemWhatsApp = String.format(
            "Olá %s, confirmamos sua consulta para hoje, %s, com a psicóloga %s. Endereço: %s",
            paciente.getNome(),
            dataHoraFormatada,
            psicologa.getNome(),
            localizacaoClinica
        );
        twilioService.enviarWhatsApp(paciente.getTelefone(), mensagemWhatsApp);
        
        // Enviar Email
        String assunto = "Confirmação de Consulta - Clínica Psico";
        String corpoHtml = String.format(
            "<h3>Olá, %s</h3>" +
            "<p>Confirmamos sua consulta para <b>hoje, %s</b>, com a psicóloga %s.</p>" +
            "<p><b>Localização:</b> %s</p>" +
            "<p>Caso precise desmarcar, entre em contato conosco.</p>",
            paciente.getNome(),
            dataHoraFormatada,
            psicologa.getNome(),
            localizacaoClinica
        );
        emailService.enviarEmailHtml(paciente.getEmail(), assunto, corpoHtml);
    }
    
    // Método auxiliar para converter Appointment para DTO
    private AppointmentResponse converterParaDTO(Appointment agendamento) {
        AppointmentResponse dto = new AppointmentResponse();
        dto.setId(agendamento.getId());
        dto.setPacienteNome(agendamento.getPaciente().getNome());
        dto.setPsicologaNome(agendamento.getPsicologa().getNome());
        dto.setHorario(agendamento.getHorario());
        dto.setStatus(agendamento.getStatus().toString());
        return dto;
    }
}
