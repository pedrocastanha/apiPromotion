CREATE TABLE clients
(
    id           SERIAL PRIMARY KEY,
    user_id      INTEGER NOT NULL,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) UNIQUE,
    phonenumber  VARCHAR(14) UNIQUE,
    product      VARCHAR(155),
    amount       DECIMAL(10, 2),
    active       BOOL,
    last_purchase DATE,
    created_at    TIMESTAMP,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
)