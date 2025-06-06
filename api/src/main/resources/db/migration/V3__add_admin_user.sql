-- Migration para adicionar o usuário administrador inicial
-- Login: pedrocastanhacosta1945@gmail.com
-- Senha: admin123 (hash abaixo)

INSERT INTO usuarios (
    nome,
    email,
    senha,
    papel,
    ativo,
    created_at,
    updated_at
) VALUES (
    'Admin Master',
    'pedrocastanhacosta1945@gmail.com',
    '$2b$12$PyXT0O/4hdXey/nhM4NmI.53XFDgR0s3UnnHt1j6XX.HZ/Sw0Iqf6', -- Senha 'admin123' hashada com bcrypt
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

