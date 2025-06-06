package org.example.service;

import org.example.model.*;
import org.example.repository.NotificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm (zz)");

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    // Inject existing services for sending messages
    @Autowired
    private EmailService emailService;

    @Autowired
    private TwilioService twilioService;

    // --- Methods to Create Notifications (Saving to DB) --- 

    @Transactional
    public Notificacao criarNotificacao(User destinatario, Agendamento agendamento, NotificacaoCanal canal, String eventoTrigger, String titulo, String mensagem, OffsetDateTime dataAgendada) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(destinatario);
        notificacao.setAgendamento(agendamento); // Can be null for non-appointment related notifications
        notificacao.setTipoCanal(canal);
        notificacao.setEventoTrigger(eventoTrigger);
        notificacao.setStatusEnvio(NotificacaoStatusEnvio.PENDENTE);
        notificacao.setDataAgendadaEnvio(dataAgendada);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);

        return notificacaoRepository.save(notificacao);
    }

    // --- Methods to Trigger Specific Notifications --- 

    // Called after a new appointment is successfully created
    public void criarNotificacaoConfirmacaoAgendamento(Agendamento agendamento) {
        User paciente = agendamento.getPaciente();
        User profissional = agendamento.getProfissional();
        String dataHoraFormatada = agendamento.getDataHoraInicio().format(FORMATTER);

        // Email Notification
        String assuntoEmail = "Confirmação de Agendamento - " + agendamento.getClinica().getNomeFantasia();
        String corpoEmail = String.format(
            "<h3>Olá, %s</h3>" +
            "<p>Seu agendamento com %s foi confirmado para <b>%s</b>.</p>" +
            "<p>Clínica: %s</p>" +
            "<p>Endereço: %s</p>" +
            "<p>Qualquer dúvida, entre em contato conosco.</p>",
            paciente.getNome(),
            profissional.getNome(),
            dataHoraFormatada,
            agendamento.getClinica().getNomeFantasia(),
            formatarEndereco(agendamento.getClinica()) // Helper to format address
        );
        criarNotificacao(paciente, agendamento, NotificacaoCanal.EMAIL, "CONFIRMACAO_AGENDAMENTO", assuntoEmail, corpoEmail, OffsetDateTime.now());

        // WhatsApp Notification
        if (paciente.getTelefone() != null && !paciente.getTelefone().isBlank()) {
            String mensagemWhatsApp = String.format(
                "Olá %s, seu agendamento na clínica %s com %s foi confirmado para %s.",
                paciente.getNome(),
                agendamento.getClinica().getNomeFantasia(),
                profissional.getNome(),
                dataHoraFormatada
            );
            criarNotificacao(paciente, agendamento, NotificacaoCanal.WHATSAPP, "CONFIRMACAO_AGENDAMENTO", null, mensagemWhatsApp, OffsetDateTime.now());
        }
    }

    // Called when a patient cancels an appointment
    public void criarNotificacaoCancelamentoParaProfissional(Agendamento agendamento) {
        User profissional = agendamento.getProfissional();
        User paciente = agendamento.getPaciente();
        String dataHoraFormatada = agendamento.getDataHoraInicio().format(FORMATTER);

        // Email Notification
        String assuntoEmail = "Cancelamento de Consulta - " + agendamento.getClinica().getNomeFantasia();
        String corpoEmail = String.format(
            "<h3>Olá, %s</h3>" +
            "<p>O paciente %s cancelou a consulta agendada para <b>%s</b>.</p>" +
            "<p><b>Motivo:</b> %s</p>",
            profissional.getNome(),
            paciente.getNome(),
            dataHoraFormatada,
            agendamento.getMotivoCancelamento()
        );
        criarNotificacao(profissional, agendamento, NotificacaoCanal.EMAIL, "CANCELAMENTO_PACIENTE", assuntoEmail, corpoEmail, OffsetDateTime.now());

        // WhatsApp Notification
        if (profissional.getTelefone() != null && !profissional.getTelefone().isBlank()) {
            String mensagemWhatsApp = String.format(
                "Atenção %s: O paciente %s cancelou a consulta de %s. Motivo: %s",
                profissional.getNome(),
                paciente.getNome(),
                dataHoraFormatada,
                agendamento.getMotivoCancelamento()
            );
            criarNotificacao(profissional, agendamento, NotificacaoCanal.WHATSAPP, "CANCELAMENTO_PACIENTE", null, mensagemWhatsApp, OffsetDateTime.now());
        }
    }

    // Called when clinic staff/professional cancels an appointment
    public void criarNotificacaoCancelamentoParaPaciente(Agendamento agendamento) {
        User paciente = agendamento.getPaciente();
        User cancelador = agendamento.getCanceladoPorUsuario();
        String dataHoraFormatada = agendamento.getDataHoraInicio().format(FORMATTER);

        // Email Notification
        String assuntoEmail = "Cancelamento de Consulta - " + agendamento.getClinica().getNomeFantasia();
        String corpoEmail = String.format(
            "<h3>Olá, %s</h3>" +
            "<p>Sua consulta agendada para <b>%s</b> com %s foi cancelada pela clínica.</p>" +
            "<p><b>Motivo:</b> %s</p>" +
            "<p>Para reagendar, entre em contato conosco.</p>",
            paciente.getNome(),
            dataHoraFormatada,
            agendamento.getProfissional().getNome(),
            agendamento.getMotivoCancelamento()
        );
        criarNotificacao(paciente, agendamento, NotificacaoCanal.EMAIL, "CANCELAMENTO_CLINICA", assuntoEmail, corpoEmail, OffsetDateTime.now());

        // WhatsApp Notification
        if (paciente.getTelefone() != null && !paciente.getTelefone().isBlank()) {
            String mensagemWhatsApp = String.format(
                "Olá %s, sua consulta na clínica %s do dia %s foi cancelada. Motivo: %s. Entre em contato para reagendar.",
                paciente.getNome(),
                agendamento.getClinica().getNomeFantasia(),
                dataHoraFormatada,
                agendamento.getMotivoCancelamento()
            );
            criarNotificacao(paciente, agendamento, NotificacaoCanal.WHATSAPP, "CANCELAMENTO_CLINICA", null, mensagemWhatsApp, OffsetDateTime.now());
        }
    }

    // TODO: Add methods for creating reminder notifications (e.g., 24h before)
    // These would typically be scheduled

    // --- Method to Process Pending Notifications --- 

    // This method can be scheduled to run periodically (e.g., every minute)
    @Transactional
    // @Scheduled(fixedRate = 60000) // Example: Run every 60 seconds
    public void processarNotificacoesPendentes() {
        OffsetDateTime agora = OffsetDateTime.now();
        log.debug("Processando notificações pendentes antes de {}", agora);

        // Find notifications that are PENDING and whose scheduled time is now or in the past
        List<Notificacao> pendentes = notificacaoRepository.findByStatusEnvioAndDataAgendadaEnvioBefore(
                NotificacaoStatusEnvio.PENDENTE, agora);

        if (pendentes.isEmpty()) {
            log.debug("Nenhuma notificação pendente encontrada.");
            return;
        }

        log.info("Encontradas {} notificações pendentes para processar.", pendentes.size());

        for (Notificacao notificacao : pendentes) {
            enviarNotificacaoAssincrona(notificacao);
        }
    }

    // Use @Async for non-blocking sending
    @Async
    @Transactional
    public void enviarNotificacaoAssincrona(Notificacao notificacao) {
        log.info("Tentando enviar notificação ID: {}, Canal: {}, Destinatário: {}", 
                 notificacao.getId(), notificacao.getTipoCanal(), notificacao.getUsuario().getEmail());
        try {
            boolean sucesso = false;
            String idExterno = null;

            if (notificacao.getUsuario() == null) {
                 throw new Exception("Destinatário não encontrado para notificação ID: " + notificacao.getId());
            }

            switch (notificacao.getTipoCanal()) {
                case EMAIL:
                    // TODO: Consider adding HTML support toggle based on message content?
                    emailService.enviarEmailHtml(notificacao.getUsuario().getEmail(), notificacao.getTitulo(), notificacao.getMensagem());
                    sucesso = true; // Assuming sendEmailHtml throws exception on failure
                    break;
                case WHATSAPP:
                    if (notificacao.getUsuario().getTelefone() != null && !notificacao.getUsuario().getTelefone().isBlank()) {
                        // Assuming twilioService returns message SID or throws exception
                        idExterno = twilioService.enviarWhatsApp(notificacao.getUsuario().getTelefone(), notificacao.getMensagem());
                        sucesso = true;
                    } else {
                        throw new Exception("Telefone do destinatário está vazio.");
                    }
                    break;
                case SMS:
                    log.warn("Canal SMS não implementado para notificação ID: {}", notificacao.getId());
                    throw new UnsupportedOperationException("Canal SMS não implementado.");
                case SISTEMA:
                    log.info("Notificação SISTEMA ID: {} marcada como LIDA (simulação de entrega)", notificacao.getId());
                    notificacao.setStatusEnvio(NotificacaoStatusEnvio.LIDO); // Mark as read/delivered for system notifications
                    sucesso = true;
                    break;
            }

            if (sucesso && notificacao.getTipoCanal() != NotificacaoCanal.SISTEMA) {
                notificacao.setStatusEnvio(NotificacaoStatusEnvio.ENVIADO);
                notificacao.setDataEfetivaEnvio(OffsetDateTime.now());
                notificacao.setIdExternoMensagem(idExterno);
                notificacao.setRespostaErro(null);
                log.info("Notificação ID: {} enviada com sucesso.", notificacao.getId());
            } else if (sucesso && notificacao.getTipoCanal() == NotificacaoCanal.SISTEMA) {
                 // Already updated status to LIDO
            }

        } catch (Exception e) {
            log.error("Falha ao enviar notificação ID: {}. Erro: {}", notificacao.getId(), e.getMessage());
            notificacao.setStatusEnvio(NotificacaoStatusEnvio.FALHA);
            notificacao.setRespostaErro(e.getMessage().substring(0, Math.min(e.getMessage().length(), 1000))); // Truncate error message if needed
        }
        // Save updated status regardless of success/failure
        notificacaoRepository.save(notificacao);
    }

    // --- Helper Methods --- 

    private String formatarEndereco(Clinica clinica) {
        if (clinica == null) return "Endereço não disponível";
        // Basic formatting, can be improved
        return String.format("%s, %s %s - %s, %s - %s, %s",
                clinica.getEnderecoLogradouro() != null ? clinica.getEnderecoLogradouro() : "",
                clinica.getEnderecoNumero() != null ? clinica.getEnderecoNumero() : "",
                clinica.getEnderecoComplemento() != null ? "(" + clinica.getEnderecoComplemento() + ")" : "",
                clinica.getEnderecoBairro() != null ? clinica.getEnderecoBairro() : "",
                clinica.getEnderecoCidade() != null ? clinica.getEnderecoCidade() : "",
                clinica.getEnderecoUf() != null ? clinica.getEnderecoUf() : "",
                clinica.getEnderecoCep() != null ? "CEP: " + clinica.getEnderecoCep() : ""
        ).replaceAll(" ,", ",").replaceAll(" - ,", " -").trim();
    }
}

