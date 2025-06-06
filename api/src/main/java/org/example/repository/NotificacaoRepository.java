package org.example.repository;

import org.example.model.Notificacao;
import org.example.model.NotificacaoStatusEnvio;
import org.example.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {

    List<Notificacao> findByUsuario(User usuario);

    List<Notificacao> findByUsuarioId(UUID usuarioId);

    // Busca notificações pendentes que deveriam ter sido enviadas antes de um certo tempo
    List<Notificacao> findByStatusEnvioAndDataAgendadaEnvioBefore(
            NotificacaoStatusEnvio status, OffsetDateTime dataLimite);

    // Busca notificações por usuário e status, paginadas (útil para UI)
    Page<Notificacao> findByUsuarioIdAndStatusEnvioOrderByDataAgendadaEnvioDesc(
            UUID usuarioId, NotificacaoStatusEnvio status, Pageable pageable);

     Page<Notificacao> findByUsuarioIdOrderByDataAgendadaEnvioDesc(
            UUID usuarioId, Pageable pageable);

    // Adicionar outros métodos de busca conforme necessário
}

