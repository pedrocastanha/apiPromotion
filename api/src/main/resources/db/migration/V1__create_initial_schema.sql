DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
CREATE TYPE user_role AS ENUM ('DONO_CLINICA','ATENDENTE','PROFISSIONAL','PACIENTE','ADMIN');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'agendamento_status') THEN
CREATE TYPE agendamento_status AS ENUM ('AGENDADO','CONFIRMADO','CANCELADO_PACIENTE','CANCELADO_CLINICA','REALIZADO','NAO_COMPARECEU');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'agendamento_recorrencia') THEN
CREATE TYPE agendamento_recorrencia AS ENUM ('UNICO','SEMANAL','QUINZENAL','MENSAL');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'atendimento_pagamento_status') THEN
CREATE TYPE atendimento_pagamento_status AS ENUM ('PENDENTE','PAGO','PARCIAL');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'excecao_horario_tipo') THEN
CREATE TYPE excecao_horario_tipo AS ENUM ('FOLGA','HORARIO_ESPECIAL');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'notificacao_canal') THEN
CREATE TYPE notificacao_canal AS ENUM ('EMAIL','WHATSAPP','SMS','SISTEMA');
END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'notificacao_status_envio') THEN
CREATE TYPE notificacao_status_envio AS ENUM ('PENDENTE','ENVIADO','FALHA','LIDO');
END IF;
END$$;

CREATE OR REPLACE FUNCTION touch_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at := NOW();
RETURN NEW;
END $$;

CREATE TABLE IF NOT EXISTS clinicas (
    id                BIGSERIAL PRIMARY KEY,
    nome_fantasia     VARCHAR(255) NOT NULL,
    razao_social      VARCHAR(255) UNIQUE,
    cnpj              VARCHAR(14)  UNIQUE,
    endereco_logradouro VARCHAR(255),
    endereco_numero   VARCHAR(50),
    endereco_complemento VARCHAR(100),
    endereco_bairro   VARCHAR(100),
    endereco_cidade   VARCHAR(100),
    endereco_uf       VARCHAR(2),
    endereco_cep      VARCHAR(8),
    telefone_principal VARCHAR(20),
    email_contato     VARCHAR(255) UNIQUE NOT NULL,
    logo_url          VARCHAR(512),
    ativa             BOOLEAN DEFAULT TRUE NOT NULL,
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS usuarios (
    id                BIGSERIAL PRIMARY KEY,
    clinica_id        BIGINT REFERENCES clinicas(id) ON DELETE SET NULL,
    nome              VARCHAR(100) NOT NULL,
    email             VARCHAR(150) UNIQUE NOT NULL,
    senha             VARCHAR(255) NOT NULL,
    telefone          VARCHAR(20),
    papel             user_role NOT NULL,
    cpf               VARCHAR(11) UNIQUE,
    data_nascimento   DATE,
    endereco_rua      VARCHAR(255),
    endereco_numero   VARCHAR(50),
    endereco_complemento VARCHAR(100),
    endereco_bairro   VARCHAR(100),
    endereco_cidade   VARCHAR(100),
    endereco_uf       VARCHAR(2),
    endereco_cep      VARCHAR(8),
    ativo             BOOLEAN DEFAULT TRUE NOT NULL,
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS pacientes (
    usuario_id        BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    motivo_procura    TEXT,
    plano_saude       VARCHAR(100),
    numero_carteirinha VARCHAR(100),
    como_conheceu     VARCHAR(255),
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS profissionais_info (
  usuario_id            BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    especialidade         VARCHAR(100) NOT NULL,
    registro_profissional VARCHAR(50) UNIQUE,
    conselho_profissional VARCHAR(20),
    created_at            TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS agendamentos (
    id                       BIGSERIAL PRIMARY KEY,
    clinica_id               BIGINT NOT NULL REFERENCES clinicas(id) ON DELETE CASCADE,
    paciente_id              BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    profissional_usuario_id  BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    data_hora_inicio         TIMESTAMPTZ NOT NULL,
    data_hora_fim            TIMESTAMPTZ NOT NULL,
    status                   agendamento_status NOT NULL DEFAULT 'AGENDADO',
    observacoes              TEXT,
    recorrencia              agendamento_recorrencia NOT NULL DEFAULT 'UNICO',
    recorrencia_id_pai       BIGINT REFERENCES agendamentos(id) ON DELETE SET NULL,
    motivo_cancelamento      TEXT,
    data_cancelamento        TIMESTAMPTZ,
    cancelado_por_usuario_id BIGINT REFERENCES usuarios(id) ON DELETE SET NULL,
    criado_por_usuario_id    BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    created_at               TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at               TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS atendimentos (
    id                BIGSERIAL PRIMARY KEY,
    agendamento_id    BIGINT UNIQUE NOT NULL REFERENCES agendamentos(id) ON DELETE CASCADE,
    descricao_sessao  TEXT NOT NULL,
    produtos_utilizados JSONB,
    observacoes_profissional TEXT,
    valor_cobrado     DECIMAL(12,2),
    status_pagamento  atendimento_pagamento_status NOT NULL DEFAULT 'PENDENTE',
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS notificacoes (
    id                    BIGSERIAL PRIMARY KEY,
    agendamento_id        BIGINT REFERENCES agendamentos(id) ON DELETE SET NULL,
    usuario_id            BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    tipo_canal            notificacao_canal NOT NULL,
    evento_trigger        VARCHAR(100) NOT NULL,
    status_envio          notificacao_status_envio NOT NULL DEFAULT 'PENDENTE',
    data_agendada_envio   TIMESTAMPTZ NOT NULL,
    data_efetiva_envio    TIMESTAMPTZ,
    titulo                VARCHAR(255),
    mensagem              TEXT NOT NULL,
    id_externo_mensagem   VARCHAR(255),
    resposta_erro         TEXT,
    created_at            TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

-- Clinicas
CREATE TRIGGER trg_clinicas_updated_at
    BEFORE UPDATE ON clinicas
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

-- Usuarios
CREATE TRIGGER trg_usuarios_updated_at
    BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

-- Pacientes
CREATE TRIGGER trg_pacientes_updated_at
    BEFORE UPDATE ON pacientes
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

-- Profissionais_info
CREATE TRIGGER trg_prof_info_updated_at
    BEFORE UPDATE ON profissionais_info
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

-- Agendamentos
CREATE TRIGGER trg_agendamentos_updated_at
    BEFORE UPDATE ON agendamentos
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

-- Atendimentos
CREATE TRIGGER trg_atendimentos_updated_at
    BEFORE UPDATE ON atendimentos
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

-- Notificacoes
CREATE TRIGGER trg_notificacoes_updated_at
    BEFORE UPDATE ON notificacoes
    FOR EACH ROW EXECUTE FUNCTION touch_updated_at();