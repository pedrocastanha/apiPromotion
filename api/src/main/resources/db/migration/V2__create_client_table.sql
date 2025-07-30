CREATE TABLE clients
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) UNIQUE,
    phoneNumber  VARCHAR(14) UNIQUE,
    product      VARCHAR(155),
    amount       DECIMAL(10, 2),
    active       BOOL,
    lastPurchase DATE,
    createdAt    TIMESTAMP
)