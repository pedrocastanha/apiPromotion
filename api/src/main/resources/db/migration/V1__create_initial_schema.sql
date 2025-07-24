CREATE TABLE IF NOT EXISTS users (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255) UNIQUE,
    phoneNumber     VARCHAR(14)  UNIQUE,
    password        VARCHAR(255)
    );
