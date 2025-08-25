CREATE TABLE IF NOT EXISTS company (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT company_name_unique UNIQUE (name)
    );

ALTER TABLE users
    RENAME COLUMN phonenumber TO phone_number;

ALTER TABLE users
    ADD COLUMN company_id INTEGER;

ALTER TABLE users
    ADD CONSTRAINT fk_company_id FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE RESTRICT;

ALTER TABLE clients
    RENAME COLUMN phonenumber TO phone_number;