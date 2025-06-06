v# Instruções de Instalação e Uso - Sistema de Agendamento para Clínica de Psicologia

## Visão Geral

Este sistema completo de agendamento para clínica de psicologia consiste em:

1. **Backend**: Desenvolvido em Java com Spring Boot
2. **Frontend**: Desenvolvido em React com TypeScript
3. **Banco de Dados**: PostgreSQL

## Requisitos

- Java 11 ou superior
- Node.js 16 ou superior
- PostgreSQL 12 ou superior
- Maven

## Instalação e Configuração

### 1. Banco de Dados

1. Crie um banco de dados PostgreSQL:
   ```sql
   CREATE DATABASE psico_agendamentos;
   ```

2. Execute o script SQL para criar as tabelas:
   ```bash
   psql -U seu_usuario -d psico_agendamentos -f V1_Create_Tables_Updated.sql
   ```

### 2. Backend

1. Extraia o arquivo `psico_agendamentos_melhorado.zip`

2. Configure o arquivo `src/main/resources/application.properties`:
   ```properties
   # Configurações do banco de dados
   spring.datasource.url=jdbc:postgresql://localhost:5432/psico_agendamentos
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   
   # Configurações da clínica
   clinica.endereco=Endereço da sua clínica
   clinica.cidade=Cidade
   clinica.cep=CEP
   clinica.complemento=Complemento (opcional)
   
   # Configurações do Twilio (WhatsApp)
   twilio.account_sid=seu_account_sid
   twilio.auth_token=seu_auth_token
   twilio.from_number=seu_numero_whatsapp
   
   # Configurações de email
   spring.mail.host=seu_servidor_smtp
   spring.mail.port=porta_smtp
   spring.mail.username=seu_email
   spring.mail.password=sua_senha
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

3. Compile e execute o backend:
   ```bash
   cd psico_agendamentos
   mvn spring-boot:run
   ```

### 3. Frontend

1. Extraia o arquivo `psico-frontend-src.zip`

2. Instale as dependências:
   ```bash
   cd psico-frontend
   npm install
   ```

3. Configure a URL da API no arquivo `src/services/api.ts`:
   ```typescript
   const API_URL = 'http://localhost:8080'; // Ajuste conforme a configuração do seu backend
   ```

4. Execute o frontend em modo de desenvolvimento:
   ```bash
   npm run dev
   ```

5. Para build de produção:
   ```bash
   npm run build
   ```

## Uso do Sistema

### Acesso Inicial

O sistema possui três tipos de usuários:
- **Administrador**: Gerencia psicólogas e visualiza todos os agendamentos
- **Psicóloga**: Visualiza agenda e gerencia consultas
- **Paciente**: Agenda e gerencia suas consultas

Para o primeiro acesso, crie um usuário administrador diretamente no banco de dados:

```sql
INSERT INTO USUARIO (nome, email, senha, telefone, papel)
VALUES ('Admin', 'admin@exemplo.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '11999999999', 'ADMIN');
```
(A senha é "password")

### Funcionalidades Principais

1. **Área do Paciente**:
   - Login/Registro
   - Visualização de consultas agendadas
   - Agendamento de consultas (simples ou recorrentes)
   - Cancelamento de consultas (com mais de 24h de antecedência)

2. **Área da Psicóloga**:
   - Login
   - Visualização da agenda do dia
   - Visualização da agenda por data
   - Cancelamento de consultas

3. **Área Administrativa**:
   - Login
   - Cadastro de psicólogas
   - Visualização de todos os agendamentos

## Regras de Negócio Implementadas

1. Cadastro por tipo de agendamento (mensal, quinzenal, semanal, variado)
2. Visualização da agenda do dia pela psicóloga
3. Cancelamento com registro de motivo
4. Restrição de cancelamento com menos de 24h de antecedência
5. Notificações completas por WhatsApp e email
6. Confirmação automática no dia da consulta com localização da clínica

## Suporte e Manutenção

Para suporte técnico ou dúvidas sobre o sistema, entre em contato com:
- Email: suporte@exemplo.com
- Telefone: (11) 9999-9999
