CREATE INDEX IF NOT EXISTS idx_usuarios_clinica_id
    ON usuarios (clinica_id);

CREATE INDEX IF NOT EXISTS idx_agendamentos_clinica_id
    ON agendamentos (clinica_id);

CREATE INDEX IF NOT EXISTS idx_agendamentos_paciente
    ON agendamentos (paciente_id);

CREATE INDEX IF NOT EXISTS idx_agendamentos_profissional
    ON agendamentos (profissional_usuario_id);

CREATE INDEX IF NOT EXISTS idx_agendamentos_recorrencia_pai
    ON agendamentos (recorrencia_id_pai);

CREATE INDEX IF NOT EXISTS idx_agendamentos_cancelado_por
    ON agendamentos (cancelado_por_usuario_id);

CREATE INDEX IF NOT EXISTS idx_agendamentos_criado_por
    ON agendamentos (criado_por_usuario_id);

CREATE INDEX IF NOT EXISTS idx_agendamentos_prof_data_inicio
    ON agendamentos (profissional_usuario_id, data_hora_inicio);

CREATE INDEX IF NOT EXISTS idx_agendamentos_status
    ON agendamentos (status);

CREATE INDEX IF NOT EXISTS idx_atendimentos_status_pagamento
    ON atendimentos (status_pagamento);

CREATE INDEX IF NOT EXISTS idx_atendimentos_created_at
    ON atendimentos (created_at);

CREATE INDEX IF NOT EXISTS idx_atendimentos_produtos_gin
    ON atendimentos USING GIN (produtos_utilizados);

CREATE INDEX IF NOT EXISTS idx_notificacoes_agendamento
    ON notificacoes (agendamento_id);

CREATE INDEX IF NOT EXISTS idx_notificacoes_usuario
    ON notificacoes (usuario_id);

CREATE INDEX IF NOT EXISTS idx_notificacoes_status_envio
    ON notificacoes (status_envio);

CREATE INDEX IF NOT EXISTS idx_notificacoes_data_agendada
    ON notificacoes (data_agendada_envio);
