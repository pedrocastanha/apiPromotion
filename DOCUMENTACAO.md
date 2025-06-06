# Documentação Completa - Sistema de Clínica Adaptado

## 1. Introdução

Este documento detalha a configuração, arquitetura, funcionalidades e instruções de uso para o sistema de gerenciamento de clínicas adaptado a partir do repositório `pedrocastanha/clinic`. O sistema foi modificado para incluir funcionalidades específicas de login para administradores e donos de clínica, gestão de agendamentos com recorrência e notificações de cancelamento.

## 2. Arquitetura e Estrutura do Projeto

O projeto está dividido em duas partes principais: o backend (API) e o frontend (Web).

### 2.1. Backend (`/api`)

Construído com Java e Spring Boot, responsável pela lógica de negócios, gerenciamento de dados e exposição da API REST.

-   `/api/src/main/java/org/example`:
    -   `config`: Configurações de segurança (Spring Security, JWT), CORS, etc.
    -   `controller`: Controladores REST que expõem os endpoints da API.
    -   `dto`: Data Transfer Objects usados para comunicação entre frontend e backend.
    -   `exception`: Manipuladores de exceções globais.
    -   `model`: Entidades JPA que representam as tabelas do banco de dados.
    -   `repository`: Repositórios Spring Data JPA para interação com o banco.
    -   `service`: Camada de serviço contendo a lógica de negócios.
-   `/api/src/main/resources`:
    -   `application.properties`: Arquivo principal de configuração do Spring Boot (banco de dados, JWT, etc.).
    -   `db/migration`: Scripts SQL de migração do banco de dados gerenciados pelo Flyway. **Importante:** Contém as definições do schema e a criação do usuário admin.

### 2.2. Frontend (`/web`)

Interface de usuário construída com React, TypeScript e Vite, utilizando a biblioteca de componentes Shadcn/UI.

-   `/web/src`:
    -   `components`: Componentes React reutilizáveis, organizados por funcionalidade (auth, admin, common, ui, etc.).
    -   `context`: Context API do React para gerenciamento de estado global (ex: autenticação).
    -   `hooks`: Hooks customizados.
    -   `lib`: Funções utilitárias.
    -   `pages`: Componentes que representam as páginas/telas da aplicação.
    -   `services`: Funções para interagir com a API backend.
    -   `types`: Definições de tipos TypeScript.
-   `public`: Arquivos estáticos.
-   `index.html`: Ponto de entrada da aplicação web.
-   `package.json`: Define as dependências e scripts do projeto Node.js.
-   `vite.config.ts`, `tailwind.config.js`, `postcss.config.js`: Arquivos de configuração do Vite, Tailwind CSS e PostCSS.

## 3. Configuração do Ambiente

### 3.1. Pré-requisitos

-   **Backend:**
    -   Java JDK (versão 17 ou superior recomendada)
    -   Maven (para build e gerenciamento de dependências)
    -   PostgreSQL (banco de dados)
-   **Frontend:**
    -   Node.js (versão 18 ou superior recomendada)
    -   npm ou yarn (gerenciador de pacotes Node.js)

### 3.2. Configuração do Backend

1.  **Banco de Dados:**
    -   Crie um banco de dados PostgreSQL.
    -   Configure as credenciais de acesso no arquivo `/api/src/main/resources/application.properties`:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_banco
        spring.datasource.username=seu_usuario_postgres
        spring.datasource.password=sua_senha_postgres
        ```
    -   O Flyway aplicará as migrations automaticamente ao iniciar a aplicação.

2.  **JWT (JSON Web Token):**
    -   Configure um segredo seguro para a geração e validação dos tokens JWT no arquivo `/api/src/main/resources/application.properties`:
        ```properties
        jwt.secret=SEU_SEGREDO_JWT_SUPER_SEGURO_AQUI
        ```
    -   Ajuste o tempo de expiração do token se necessário (`jwt.expiration=...` em milissegundos).

3.  **Notificações (Email/WhatsApp - A Implementar):**
    -   **Email:** Quando a funcionalidade de notificação por email for implementada, será necessário configurar as credenciais do servidor SMTP em `application.properties` (ex: host, porta, usuário, senha).
    -   **WhatsApp:** A integração com WhatsApp geralmente requer uma API de terceiros (como a API oficial do WhatsApp Business via Meta ou provedores como Twilio). As credenciais (Account SID, Auth Token, número de telefone) deverão ser configuradas, preferencialmente via variáveis de ambiente ou um arquivo de configuração seguro, e referenciadas em `application.properties`.
        *Exemplo (a ser adaptado conforme a API escolhida):*
        ```properties
        # Exemplo para Twilio (necessário adicionar dependência e implementar serviço)
        # twilio.accountSid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        # twilio.authToken=seu_auth_token
        # twilio.phoneNumber=+15551234567
        ```

### 3.3. Configuração do Frontend

1.  **URL da API:**
    -   Verifique se a URL base da API backend está corretamente configurada nos serviços do frontend (geralmente em `/web/src/services/api.ts` ou similar). Por padrão, pode estar configurado para `http://localhost:8080`.

## 4. Executando o Sistema

### 4.1. Backend (API)

1.  Navegue até o diretório `/api`.
2.  Compile e execute a aplicação Spring Boot usando Maven:
    ```bash
    mvn spring-boot:run
    ```
    Ou compile em um JAR e execute:
    ```bash
    mvn clean package
    java -jar target/clinic-api-0.0.1-SNAPSHOT.jar # O nome do JAR pode variar
    ```
    A API estará rodando, por padrão, em `http://localhost:8080`.

### 4.2. Frontend (Web)

1.  Navegue até o diretório `/web`.
2.  Instale as dependências:
    ```bash
    npm install
    # ou
    # yarn install
    ```
3.  Inicie o servidor de desenvolvimento:
    ```bash
    npm run dev
    # ou
    # yarn dev
    ```
    A aplicação frontend estará acessível, por padrão, em `http://localhost:5173` (ou outra porta indicada pelo Vite).

## 5. Endpoints da API

A API utiliza autenticação baseada em JWT. Requisições para endpoints protegidos devem incluir o token no cabeçalho `Authorization` como `Bearer <token>`.

-   **Autenticação (`/auth`)**
    -   `POST /auth/register`: Registra um novo usuário (paciente). (Público)
        -   *Body:* `RegistrationRequest` (nome, email, senha, telefone)
    -   `POST /auth/login`: Autentica um usuário e retorna um token JWT. (Público)
        -   *Body:* `LoginRequest` (email, senha)
        -   *Response:* `LoginResponse` (token)

-   **Admin (`/admin`)** - Requer Role: `ADMIN`
    -   *(Endpoints a serem detalhados conforme implementação)* - Ex: Gerenciamento de usuários, clínicas, etc.

-   **Dono da Clínica (`/clinica` ou similar)** - Requer Role: `DONO_CLINICA`
    -   *(Endpoints a serem detalhados conforme implementação)*
    -   `GET /agendamentos/minha-agenda`: Retorna os agendamentos da clínica do usuário logado.
    -   `POST /agendamentos`: Cria um novo agendamento (incluindo recorrência).
    -   `PUT /agendamentos/{id}/cancelar`: Cancela um agendamento (com motivo).
    -   `GET /agendamentos/{id}`: Obtém detalhes de um agendamento.

-   **Paciente (`/paciente`)** - Requer Role: `PACIENTE`
    -   *(Endpoints existentes no projeto original, verificar necessidade de adaptação)*

-   **Profissional (`/profissional`)** - Requer Role: `PROFISSIONAL`
    -   *(Endpoints existentes no projeto original, verificar necessidade de adaptação)*

*(Nota: Os endpoints específicos para Dono da Clínica, Paciente e Profissional precisam ser revisados e adaptados/implementados conforme os requisitos detalhados)*

## 6. Banco de Dados e Migrations

-   O sistema utiliza PostgreSQL como banco de dados.
-   As migrations do schema são gerenciadas pelo Flyway e estão localizadas em `/api/src/main/resources/db/migration`.
-   As migrations são aplicadas automaticamente na inicialização do backend.
-   **Migrations Principais:**
    -   `V1__create_initial_schema.sql`: Cria as tabelas iniciais (clinicas, usuarios, pacientes, profissionais_info, agendamentos, atendimentos, notificacoes) e tipos ENUM.
    -   `V2__create_indexes.sql`: Cria índices para otimização de consultas.
    -   `V3__add_admin_user.sql`: **Adiciona o usuário administrador inicial.**
        -   **Login:** `pedrocastanhacosta1945@gmail.com`
        -   **Senha:** `admin123`
        -   **Role:** `ADMIN`

## 7. Funcionalidades Implementadas (Resumo)

-   **Autenticação:** Sistema de login baseado em email e senha com JWT para diferentes papéis (Admin, Dono da Clínica, Paciente, Profissional).
-   **Usuário Admin:** Criação de um usuário administrador padrão via migration.
-   **Gestão de Agendamentos (Dono da Clínica):**
    -   Visualização da agenda.
    -   Criação de agendamentos (único, semanal, quinzenal, mensal).
    -   Cancelamento de agendamentos com registro de motivo.
    -   Visualização de detalhes do agendamento/cliente.
-   **Notificações (Cancelamento):**
    -   Backend preparado para enviar notificações por email e WhatsApp (requer implementação e configuração dos serviços específicos).
-   **Frontend:** Interface adaptada para login e funcionalidades do dono da clínica.

## 8. Próximos Passos e Considerações

-   Implementar completamente os serviços de notificação por Email e WhatsApp, incluindo a configuração segura das credenciais.
-   Refinar e completar a implementação dos endpoints específicos para cada papel (Admin, Dono Clínica, Paciente, Profissional) no backend.
-   Concluir a adaptação e melhoria da interface do frontend para todas as funcionalidades.
-   Realizar testes abrangentes (unitários, integração, E2E).
-   Considerar adicionar mecanismos de segurança adicionais (ex: 2FA, rate limiting) se necessário.

